package com.open_nlp.poc.nlp.domain;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.springframework.stereotype.Component;

import java.util.List;

public class NERDomain {

    DocumentCategorizerME categorizer;
    NameFinderME[] nameFinderMEs;

    public NERDomain(DoccatModel doccatModel) {
        this.categorizer = new DocumentCategorizerME(doccatModel);
    }

    public void populateNameFinder(List<TokenNameFinderModel> tokenNameFinderModels){
        nameFinderMEs = new NameFinderME[tokenNameFinderModels.size()];
        for (int i = 0; i < tokenNameFinderModels.size(); i++) {
            nameFinderMEs[i] = new NameFinderME(tokenNameFinderModels.get(i));
        }
    }

    public DocumentCategorizerME getCategorizer() {
        return categorizer;
    }

    public NameFinderME[] getNameFinderMEs() {
        return nameFinderMEs;
    }
}
