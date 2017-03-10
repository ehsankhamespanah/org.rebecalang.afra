package org.rebecalang.afra.ideplugin.view.modelcheckreport;

public class JaxBObjectCreator {

    public static void main(String[] args) throws Throwable {
//        String[] arg = {
//        		"-p", "org.rebecalang.afra.ideplugin.view.modelcheckreport.resultobjectmodel", 
//        		"-d", "src", 
//        		"src/org/rebecalang/afra/ideplugin/view/modelcheckreport/modelcheckingresult.xsd"};

//        String[] arg = {
//        		"-p", "org.rebecalang.afra.ideplugin.view.modelcheckreport.counterexamplestateobjectmodel", 
//        		"-d", "src", 
//        		"src/org/rebecalang/afra/ideplugin/view/modelcheckreport/state.xsd"};
        
        String[] arg = {
        		"-p", "org.rebecalang.afra.ideplugin.view.modelcheckreport.counterexampletransitionobjectmodel", 
        		"-d", "src", 
        		"src/org/rebecalang/afra/ideplugin/view/modelcheckreport/transition.xsd"};
        
    	com.sun.tools.xjc.XJCFacade.main(arg);
        
    }
}

