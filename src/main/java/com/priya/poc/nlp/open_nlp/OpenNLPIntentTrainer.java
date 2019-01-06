package com.priya.poc.nlp.open_nlp;

import com.priya.poc.nlp.domain.NERDomain;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.namefind.*;
import opennlp.tools.util.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenNLPIntentTrainer implements ApplicationListener<ContextRefreshedEvent> {

    public static final String trainingDirectoryLocation = "example/weather/train";

    NERDomain nerDomain;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<ObjectStream<DocumentSample>> categoryStreams = new ArrayList<ObjectStream<DocumentSample>>();
        File trainingDirectory = new File(getClass().getClassLoader().getResource(trainingDirectoryLocation).getFile());
        try {
            for (File trainingFile : trainingDirectory.listFiles()) {
                String intent = trainingFile.getName().replaceFirst("[.][^.]+$", "");
                ObjectStream<String> lineStream = null;
                lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainingFile), "UTF-8");
                ObjectStream<DocumentSample> documentSampleStream = new BLKIntentDocumentStream(intent, lineStream);
                categoryStreams.add(documentSampleStream);
            }

            ObjectStream<DocumentSample> combinedDocumentSampleStream = ObjectStreamUtils.concatenateObjectStream(categoryStreams);

            TrainingParameters trainingParams = new TrainingParameters();
            trainingParams.put(TrainingParameters.ITERATIONS_PARAM, 10);
            trainingParams.put(TrainingParameters.CUTOFF_PARAM, 0);

            DoccatModel doccatModel = DocumentCategorizerME.train("en", combinedDocumentSampleStream, trainingParams, new DoccatFactory());
            combinedDocumentSampleStream.close();

            nerDomain = new NERDomain(doccatModel);

            //NamedEntityRecognition
            //Eg: Jim bought 300 shares of Acme Corp. in 2006.
            //[Jim]Person bought 300 shares of [Acme Corp.]Organization in [2006]Time.
            List<TokenNameFinderModel> tokenNameFinderModels = new ArrayList<TokenNameFinderModel>();

            List<String> slots = new ArrayList();
            slots.add("city");

            for (String slot : slots) {
                List<ObjectStream<NameSample>> nameStreams = new ArrayList<ObjectStream<NameSample>>();
                for (File trainingFile : trainingDirectory.listFiles()) {
                    ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainingFile), "UTF-8");
                    ObjectStream<NameSample> nameSampleStream = new NameSampleDataStream(lineStream);
                    nameStreams.add(nameSampleStream);
                }
                ObjectStream<NameSample> combinedNameSampleStream = ObjectStreamUtils.concatenateObjectStream(nameStreams);

                TokenNameFinderModel tokenNameFinderModel = NameFinderME.train("en", slot, combinedNameSampleStream, trainingParams, new TokenNameFinderFactory());
                combinedNameSampleStream.close();
                tokenNameFinderModels.add(tokenNameFinderModel);
            }


            nerDomain.populateNameFinder(tokenNameFinderModels);

            System.out.println("Training complete. Ready.");
            System.out.print(">");
        }catch (Exception e){
            System.out.println("Some exception occurred: "+e);
        }
    }

    public NERDomain getNerDomain() {
        return nerDomain;
    }
}
