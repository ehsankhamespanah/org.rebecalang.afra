package org.rebecalang.ide.afra.xmlparser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import javax.swing.tree.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class XMLTreeView {

       private SAXTreeBuilder saxTree = null;
       private static String file = "";
       
       public static void main(String addr){
              JFrame frame = new JFrame("Model Check Tree View");
              frame.setSize(400,400);
              
              frame.addWindowListener(new WindowAdapter(){
                   public void windowClosing(WindowEvent ev){
                       System.exit(0);
                   }
              });
              file = addr;
              new XMLTreeView(frame);
       }
       
       public XMLTreeView(JFrame frame){ 
              frame.getContentPane().setLayout(new BorderLayout());  
              DefaultMutableTreeNode top = new DefaultMutableTreeNode();          
              saxTree = new SAXTreeBuilder(top); 
              
              try {
            	  SAXParserFactory factory = SAXParserFactory.newInstance();
            	  SAXParser saxParser = factory.newSAXParser();
            	  saxParser.parse(new InputSource(new FileInputStream(file)) , saxTree);
              }catch(Exception ex){
                  top.add(new DefaultMutableTreeNode(ex.getMessage()));
              }
              JTree tree = new JTree(saxTree.getTree());
              JScrollPane scrollPane = new JScrollPane(tree);
              
              frame.getContentPane().add("Center",scrollPane);                                 
              frame.setVisible(true);      
              
        } 
        
     
}
class SAXTreeBuilder extends DefaultHandler{
       
       private DefaultMutableTreeNode currentNode = null;
       private DefaultMutableTreeNode previousNode = null;
       private DefaultMutableTreeNode rootNode = null;

       public SAXTreeBuilder(DefaultMutableTreeNode root){
              rootNode = root;
       }
       public void startDocument(){
              currentNode = rootNode;
       }
       public void endDocument(){
       }
       public void characters(char[] data,int start,int end){
              String str = new String(data,start,end);
              if (!str.equals("") && (Character.isLetter(str.charAt(0)) || Character.isDigit(str.charAt(0))))
                  currentNode.add(new DefaultMutableTreeNode(str));           
       }
       public void startElement(String uri,String qName,String lName,Attributes atts){
              previousNode = currentNode;
              currentNode = new DefaultMutableTreeNode(lName);
              // Add attributes as child nodes //
              attachAttributeList(currentNode,atts);
              previousNode.add(currentNode);              
       }
       public void endElement(String uri,String qName,String lName){
              if (currentNode.getUserObject().equals(lName))
                  currentNode = (DefaultMutableTreeNode)currentNode.getParent();              
       }
       public DefaultMutableTreeNode getTree(){
              return rootNode;
       }
       
       private void attachAttributeList(DefaultMutableTreeNode node,Attributes atts){
               for (int i=0;i<atts.getLength();i++){
                    String name = atts.getLocalName(i);
                    String value = atts.getValue(name);
                    node.add(new DefaultMutableTreeNode(name + " = " + value));
               }
       }
       
}