package utils;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class PrepGazzetteer {

    static String in_path = "./resources/senticon/";
    static String out_path = "./resources/lists/";
    static String[] in_filenames = new String[]{"senticon.en.xml", "senticon.es.xml"};
    static String[] out_filenames = new String[]{"neg_english.lst", "pos_english.lst", "neg_spanish.lst", "pos_spanish.lst"};

    public static void main(String[] args) throws Exception {
        ArrayList<String> posen = new ArrayList();
        ArrayList<String> negen = new ArrayList();
        ArrayList<String> poses = new ArrayList();
        ArrayList<String> neges = new ArrayList();

        for (String in_file : in_filenames) {
            File file = new File(in_path + in_file);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            NodeList positives = document.getElementsByTagName("positive");
            for (int i = 0; i < positives.getLength(); i++) {
                Node currentNode = positives.item(i).getFirstChild();
                while (currentNode != null) {
                    if (currentNode.getNodeName().equals("lemma")) {
                        NamedNodeMap attributes = currentNode.getAttributes();
                        String pol = String.valueOf(attributes.getNamedItem("pol"));
                        double polarity = extractPolarity(pol);
                        if (polarity >= 0.5) {
                            String text = currentNode.getFirstChild().getTextContent();
                            String word = text.replace(" ", "");
                            if (word.matches(".*[a-zA-Z]+.*") && word.indexOf('-') == -1 && word.indexOf('_') == -1) {
                                if (in_file.equals(in_filenames[0])) {
                                    System.out.println("adding " + word);
                                    posen.add(word);
                                } else {
                                    poses.add(word);
                                }
                            }
                        }

                    }
                    currentNode = currentNode.getNextSibling();
                }
            }

            NodeList negatives = document.getElementsByTagName("negative");
            for (int i = 0; i < negatives.getLength(); i++) {
                Node currentNode = negatives.item(i).getFirstChild();
                while (currentNode != null) {
                    if (currentNode.getNodeName().equals("lemma")) {
                        NamedNodeMap attributes = currentNode.getAttributes();
                        String pol = String.valueOf(attributes.getNamedItem("pol"));
                        double polarity = extractPolarity(pol);
                        if (polarity <= -0.5) {
                            String text = currentNode.getFirstChild().getTextContent();
                            String word = text.replace(" ", "");
                            if (word.matches(".*[a-zA-Z]+.*") && word.indexOf('-') == -1 && word.indexOf('_') == -1) {
                                if (in_file.equals(in_filenames[0])) {
                                    System.out.println("adding " + word);
                                    negen.add(word);
                                } else {
                                    neges.add(word);
                                }
                            }
                        }

                    }
                    currentNode = currentNode.getNextSibling();
                }
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(out_path + out_filenames[0]));
        for (String str : negen) {
            writer.write(str + '\n');
        }
        writer.close();

        writer = new BufferedWriter(new FileWriter(out_path + out_filenames[1]));
        for (String str : posen) {
            writer.write(str + '\n');
        }
        writer.close();

        writer = new BufferedWriter(new FileWriter(out_path + out_filenames[2]));
        for (String str : neges) {
            writer.write(str + '\n');
        }
        writer.close();

        writer = new BufferedWriter(new FileWriter(out_path + out_filenames[3]));
        for (String str : poses) {
            writer.write(str + '\n');
        }
        writer.close();
    }

    public static double extractPolarity(String pol) {
        pol = pol.replace("pol=\"", "");
        pol = pol.replace("\"", "");
        return Double.parseDouble(pol);
    }
}
