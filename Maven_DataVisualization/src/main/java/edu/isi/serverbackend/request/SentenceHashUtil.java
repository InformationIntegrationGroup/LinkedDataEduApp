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
    private static HashMap<String, SentenceType> sentenceHash;
    public static enum SentenceType {presPossesive, presAdjPrep, pastReg};
    
    static {
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

    public SentenceHashUtil() {
        
    }

    public String parseSentence(String relation, int inverse, ServletContext context, String type){
        Tokenizer _tokenizer = null;
        POSTaggerME tagger = null;
        String sentence = "";
        InputStream modelIn = null;
        type = type.substring(type.lastIndexOf('/')+1, type.length());
        try {
            // Loading tokenizer model
            modelIn = context.getResourceAsStream("/WEB-INF/en-pos-maxent.bin");
            final POSModel posModel = new POSModel(modelIn);

           tagger = new POSTaggerME(posModel);

            //special cases
            if(relation.equals("museum"))
                relation = "location";

            String[] sent = relation.split("(?<!^)(?=[A-Z])");
            String tags[] = tagger.tag(sent);

            modelIn.close();
            SentenceType sentenceType = sentenceHash.get(relation);

            String relationPOS = tags[0];
             if(sentenceType == SentenceType.presAdjPrep) {
                    // Todo: what would the  inverse be?
                    String verb = sent[0];
                    if (sent.length > 1) {
                        if(tags[1].equals("IN") || tags[1].equals("TO"))//we have a preposition
                            sentence = " is " + verb + " " + sent[1].toLowerCase() + " ";
                    } else {
                        if(verb.endsWith("ive"))
                            verb = verb.replaceAll("ive", "ed");
                        else if(verb.endsWith("ing"))
                            verb = verb.replaceAll("ing", "ed");
                        else if(verb.endsWith("ion"))
                            verb = verb.replaceAll("ion", "ed");
                        if(!verb.endsWith("ed"))
                            verb += "ed";

                        String prep = "to";
                        if(type.equals("Place"))
                            prep = "in";
                        if(type.equals("Person") && !relation.equals("relative"))
                            prep = "by";
                        sentence = " is " + verb + " " + prep + " ";
                    }
                } else if(sentenceType == SentenceType.presPossesive) {
                    if (inverse == 0) { // S's noun is O
                        if (sent.length > 1) {
                            sentence = "'s " + sent[0] + " " + sent[1].toLowerCase() + " is ";
                        } else {
                            sentence = "'s " + sent[0] + " is ";
                        }
                    } else { // O is the noun of S
                        if(sent.length > 1) {
                            sentence = " is the " + sent[0] + " " + sent[1].toLowerCase() + " of ";
                        }
                        else
                            sentence = " is the " + sent[0] + " of ";
                    }
                } else {
                    String verb = sent[0];
                    if (!tags[0].equals("VBN") && !tags[0].equals("VBD")) {
                        if(verb.endsWith("ive"))
                            verb = verb.replaceAll("ive", "ed");
                        else if(verb.endsWith("ing"))
                            verb = verb.replaceAll("ing", "ed");
                        else if(verb.endsWith("ion"))
                            verb = verb.replaceAll("ion", "ed");
                        if(!verb.endsWith("ed"))
                            verb += "ed";
                    }
                    if (inverse == 0) { // S past tense verb O
                        sentence = " " + verb + " ";
                    } else { // O was past tense verb by S
                        String prep = "to";
                        if(type.equals("Place"))
                            prep = "in";
                        if(type.equals("Person") && verb.equals("authored"))
                            prep = "by";
                        sentence = " was " + verb + " " + prep + " ";
                    }
                }
             
            return sentence;

        } catch (final Exception ioe) {
            ioe.printStackTrace();
        }
       
        return sentence;
    }
}
