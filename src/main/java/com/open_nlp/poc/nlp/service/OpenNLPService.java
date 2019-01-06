package com.open_nlp.poc.nlp.service;

import com.open_nlp.poc.nlp.open_nlp.OpenNLPIntentTrainer;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class OpenNLPService {

    @Autowired
    OpenNLPIntentTrainer trainer;

    Tokenizer tokenizer;

    public OpenNLPService() throws IOException {
        InputStream modelIn = new FileInputStream(getClass().getClassLoader().getResource("models/en-token.bin").getFile());
        TokenizerModel model = new TokenizerModel(modelIn);
        tokenizer = new TokenizerME(model);
    }

    public String categorizeQuery(String userQuery){

        DocumentCategorizerME categorizer = trainer.getNerDomain().getCategorizer();

        double[] outcome = categorizer.categorize(tokenizer.tokenize(userQuery));

        StringBuilder outputString = new StringBuilder("{ action: '" + categorizer.getBestCategory(outcome) + "', args: { ");

        String[] tokens = tokenizer.tokenize(userQuery);
        for (NameFinderME nameFinderME : trainer.getNerDomain().getNameFinderMEs()) {
            Span[] spans = nameFinderME.find(tokens);
            String[] names = Span.spansToStrings(spans, tokens);
            for (int i = 0; i < spans.length; i++) {
                if(i > 0) { System.out.print(", "); }
                outputString.append(spans[i].getType() + ": '" + names[i] + "' ");
            }
        }
        return outputString.toString();
    }
}
