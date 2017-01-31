package org.rebecalang.ide.afra;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import org.rebecalang.ide.afra.natures.RebecaNature;
import org.rebecalang.ide.afra.natures.SampleBuilder;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;

 
public class RebecaProjectSupport {
    /**
     * For this marvelous project we need to:
     * - create the default Eclipse project
     * - add the custom project nature
     * - create the folder structure
     *
     * @param projectName
     * @param location
     * @param natureId
     * @return
     * @throws CoreException 
     */
    public static IProject createProject(String projectName, URI location , String type , String version){
        Assert.isNotNull(projectName);
        Assert.isTrue(projectName.trim().length() > 0);

        IProject project = createBaseProject(projectName, location);
        
        try {
            addNature(project , type , version);
 
            String[] pathSrc = {"src"}; //$NON-NLS-1$ //$NON-NLS-2$
            addToProjectStructure(project, pathSrc , "folder");
            
            String[] pathOut = {"out"}; //$NON-NLS-1$ //$NON-NLS-2$
            addToProjectStructure(project, pathOut , "folder");
            
            
            String[] path_files = {"src/" + projectName + ".property" , "src/" + projectName + ".rebeca"}; //$NON-NLS-1$ //$NON-NLS-2$
            addToProjectStructure(project, path_files , "file");
            
        } catch (CoreException e) {
            e.printStackTrace();
            project = null;
        }
        return project;
    }
 
    /**
     * Just do the basics: create a basic project.
     *
     * @param location
     * @param projectName
     */
    private static IProject createBaseProject(String projectName, URI location) {
        // it is acceptable to use the ResourcesPlugin class
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
 
        if (!newProject.exists()) {
            URI projectLocation = location;
            IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
            if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
                projectLocation = null;
            }
 
            desc.setLocationURI(projectLocation);
            
            try {
                newProject.create(desc, null);
                if (!newProject.isOpen()) {
                    newProject.open(null);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
 
        return newProject;
    }
 
    private static void createFolder(IFolder folder) throws CoreException {
        IContainer parent = folder.getParent();
        if (parent instanceof IFolder) {
            createFolder((IFolder) parent);
        }
        if (!folder.exists()) {
            folder.create(false, true, null);
        }
    }
    
    private static void createFile(IFile file) throws CoreException {
    	if (!file.exists()) {
    	    byte[] bytes = "//Write your code here !".getBytes();
    	    InputStream source = new ByteArrayInputStream(bytes);
    	    file.create(source, IResource.NONE, null);
    	}
    }
 
    /**
     * Create a folder structure with a parent root, overlay, and a few child
     * folders.
     *
     * @param newProject
     * @param paths
     * @throws CoreException
     */
    private static void addToProjectStructure(IProject newProject, String[] paths , String type) throws CoreException {
        if(type == "folder"){
        	for (String path : paths) {
        		IFolder etcFolders = newProject.getFolder(path);
        		createFolder(etcFolders);
        	}
        }else if (type == "file"){
        	for (String path : paths) {
        		IFile etcFiles = newProject.getFile(path);
        		createFile(etcFiles);
        	}
        }
    }
 
    private static void addNature(IProject project , String type , String version) throws CoreException {
    	
        if (!project.hasNature(RebecaNature.NATURE_ID)) {
            IProjectDescription description = project.getDescription();
            ICommand[] commands = description.getBuildSpec();

    		for (int i = 0; i < commands.length; ++i) {
    			if (commands[i].getBuilderName().equals(SampleBuilder.BUILDER_ID)) {
    				return;
    			}
    		}
    		ICommand[] newCommands = new ICommand[commands.length + 1];
    		ICommand command = description.newCommand();
    		command.setBuilderName(SampleBuilder.BUILDER_ID);
    		newCommands[newCommands.length - 1] = command;
    		description.setBuildSpec(newCommands);
    		
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = RebecaNature.NATURE_ID;
            description.setNatureIds(newNatures);
            

            IProgressMonitor monitor = null;
            project.setDescription(description, monitor);
            project.setPersistentProperty(new QualifiedName("rmc.modelcheck", "projectVersion"), version);
			project.setPersistentProperty(new QualifiedName("rmc.modelcheck", "projectType"), type);
        }
    }
 
}