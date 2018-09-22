package com.graduate.zl.sd2Lts.parse;

import com.graduate.zl.sd2Lts.common.Constants;
import com.graduate.zl.sd2Lts.model.SeqDiagram.*;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * Created by Vincent on 2018/7/27.
 */
public class ParseXmi {

    private String fileName;

    private Document document;

    private Map<String, Lifeline> lifelines;

    private Map<String,  OccurrenceSpecificationFragment> osFragments;

    private Map<String, CombinedFragment> combinedFragments;

    private Map<String, Message> messages;

    @Getter @Setter
    private SequenceDiagram sequenceDiagram;

    public ParseXmi(String fileName) {
        this.fileName = fileName;
        this.document = loadDocument();
        this.lifelines = new HashMap<String, Lifeline>();
        this.messages = new LinkedHashMap<String, Message>();
        this.osFragments = new HashMap<String, OccurrenceSpecificationFragment>();
        this.combinedFragments = new HashMap<String, CombinedFragment>();
        this.sequenceDiagram = new SequenceDiagram();
    }

    public Document loadDocument() {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(this.fileName));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    public void parseXmi() {
        Element root = this.document.getRootElement();
        List<Element> firstLevel = root.elements();
        Element UML_MODEL = firstLevel.get(1);
        Element UML_PACKAGE = (Element) UML_MODEL.elements().get(0);
        Element UML_COLLABORATION = (Element) UML_PACKAGE.elements().get(0);
        Element ownedBehavior = (Element) UML_COLLABORATION.elements().get(0);
        List<Element> allElements = ownedBehavior.elements();
        for(Element element : allElements) {
            String elementName = element.getName();
            if (elementName.equals(Constants.LIFELINE)) {
                Lifeline lifeline = new Lifeline(element.attribute("id").getValue(), element.attribute("name").getValue());
                this.lifelines.put(lifeline.getId(), lifeline);
            } else if(elementName.equals(Constants.FRAGMENT)) {
                String type = element.attribute("type").getValue();
                if(type.equals(Constants.OCCURRENCE_SPECIFICATION)) {
                    String osId = element.attribute("id").getValue();
                    OccurrenceSpecificationFragment osf = new OccurrenceSpecificationFragment(osId, element.attribute("covered").getValue());
                    this.osFragments.put(osId, osf);
                }else if(type.equals(Constants.COMBINED_FRAGMENT)){
                    parseCombinedFragment(element);
                }
            } else if(elementName.equals(Constants.MESSAGE)) {
                String mid = element.attribute("id").getValue();
                Message message = new Message(mid, element.attribute("name").getValue(), element.attribute("sendEvent").getValue(), element.attribute("receiveEvent").getValue());
                this.messages.put(mid, message);
            }
        }
        setSequenceDiagram();
    }

    private void parseCombinedFragment(Element cf) {
        CombinedFragment ret = new CombinedFragment();

        String cfId = cf.attribute("id").getValue();
        ret.setId(cfId);
        ret.setType(cf.attribute("interactionOperator").getValue());
        List<InteractionOperand> retOperands = new ArrayList<InteractionOperand>();

        List<Element> cfElements = cf.elements();
        for(Element element : cfElements) {
            String elementName = element.getName();
            if(elementName.equals(Constants.OPERAND)) {
                InteractionOperand operand = new InteractionOperand();
                List<OccurrenceSpecificationFragment> operandCFs = new ArrayList<OccurrenceSpecificationFragment>();
                List<Element> opeElements = element.elements();
                for(Element element1 : opeElements) {
                    String elementName1 = element1.getName();
                    if(elementName1.equals(Constants.GUARD)) {
                        String guardId = element1.attribute("id").getValue();
                        String guardBody = ((Element)(element1.elements().get(0))).attribute("body").getValue();
                        Guard guard = new Guard(guardId, guardBody);
                        operand.setGuard(guard);
                    }else if(elementName1.equals(Constants.OCCURRENCE_SPECIFICATION)) {
                        OccurrenceSpecificationFragment osf = new OccurrenceSpecificationFragment(element1.attribute("id").getValue(), element1.attribute("covered").getValue());
                        operandCFs.add(osf);
                    }
                }
                operand.setOsFragments(operandCFs);
                retOperands.add(operand);
            }
        }
        ret.setOperandList(retOperands);
        this.combinedFragments.put(cfId, ret);
    }

    private void setSequenceDiagram() {
        this.sequenceDiagram.setLifelines(this.lifelines);
        this.sequenceDiagram.setOsFragments(this.osFragments);
        this.sequenceDiagram.setCombinedFragments(this.combinedFragments);
        this.sequenceDiagram.setMessages(this.messages);
    }

    public static void main(String[] args) {
        ParseXmi parseXmi = new ParseXmi("C:\\Users\\Vincent\\Desktop\\test.xml");
        parseXmi.parseXmi();
    }
}
