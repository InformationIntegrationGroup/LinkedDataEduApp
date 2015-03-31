package edu.isi.serverbackend.request;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Dipa on 3/22/2015.
 */
public final class SentenceHashUtil {
    private HashMap<String, SentenceType> sentenceHash;
    public static enum SentenceType {presPossesive, presAdjPrep, pastReg};

    public SentenceHashUtil() {
        sentenceHash = new HashMap<String, SentenceType>();

        sentenceHash.put("nationality", SentenceType.presPossesive);
        sentenceHash.put("spouse", SentenceType.presPossesive);
        sentenceHash.put("parent", SentenceType.presPossesive);
        sentenceHash.put("country", SentenceType.presPossesive);
        sentenceHash.put("department", SentenceType.presPossesive);
        sentenceHash.put("movement", SentenceType.presPossesive);
        sentenceHash.put("relative", SentenceType.presAdjPrep);
        sentenceHash.put("location", SentenceType.presAdjPrep);
        sentenceHash.put("knownFor", SentenceType.presAdjPrep);
        sentenceHash.put("training", SentenceType.pastReg);
        sentenceHash.put("influenced", SentenceType.pastReg);
        sentenceHash.put("influencedBy", SentenceType.pastReg);
        sentenceHash.put("author", SentenceType.pastReg);
        sentenceHash.put("deathPlace", SentenceType.presPossesive);
        sentenceHash.put("birthPlace", SentenceType.presPossesive);
        sentenceHash.put("leaderName", SentenceType.presPossesive);
    }

    public String parseSentence(String relation, int inverse, ServletContext context){
        Tokenizer _tokenizer = null;
        POSTaggerME tagger = null;
        String sentence = "";
        InputStream modelIn = null;
        try {
            // Loading tokenizer model
            modelIn = context.getResourceAsStream("/WEB-INF/en-pos-maxent.bin");
            final POSModel posModel = new POSModel(modelIn);

           tagger = new POSTaggerME(posModel);

            String[] sent = relation.split("(?<!^)(?=[A-Z])");
            String tags[] = tagger.tag(sent);

            modelIn.close();
            SentenceType sentenceType = sentenceHash.get(relation);

            String relationPOS = tags[0];
            switch (sentenceType) {
                case presAdjPrep: {
                    // Todo: what would the  inverse be?
                    String nounToVerb = sent[0];
                    if (sent.length == 2) {
                        if(tags[1].equals("IN") || tags[1].equals("TO"))//we have a preposition
                            sentence = " is " + nounToVerb + " " + sent[1].toLowerCase() + " ";
                    } else {
                        nounToVerb = nounToVerb.replaceAll("ion", "ed");
                        nounToVerb = nounToVerb.replaceAll("ive", "ed");
                        nounToVerb = nounToVerb.replaceAll("ing", "ed");
                        String prep = "to";
                        sentence = " is " + nounToVerb + " " + prep + " ";
                    }
                }
                case presPossesive: {
                    if (inverse == 0) { // S's noun is O
                        if (sent.length == 2) {
                            sentence = "'s " + sent[0] + " " + sent[1].toLowerCase() + " is ";
                            System.out.println("here");
                        } else {
                            sentence = "'s " + sent[0] + " is ";
                        }
                        System.out.println(sentence);
                    } else { // O is the noun of S
                        if(sent.length == 2) {
                            sentence = " is the " + sent[0] + " " + sent[1].toLowerCase() + " of ";
                            System.out.println("here");
                        }
                        else
                            sentence = " is the " + sent[0] + " of ";
                        System.out.println(sentence);
                    }
                }
                case pastReg: {
                    String verb = sent[0];
                    if (!tags[0].equals("VBN") && !tags[0].equals("VBD")) {
                        verb = verb.replaceAll("ive", "ed");
                        verb = verb.replaceAll("ing", "ed");
                        verb = verb.replaceAll("ion", "ed");
                    }
                    if (inverse == 0) { // S past tense verb O
                        sentence = " " + verb + " ";
                    } else { // O was past tense verb by S
                        sentence = " was " + verb + " by ";
                    }
                }
            }

            return sentence;

        } catch (final Exception ioe) {
            ioe.printStackTrace();
        }

        return sentence;
    }
}
