/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.niftyGui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import eu.opends.main.DriveAnalyzer;
import eu.opends.niftyGui.listBox.TextListBoxModel;

/**
 * 
 * @author Rafael Math
 */
public class AnalyzerFileSelectionGUIController implements ScreenController
{
	private DriveAnalyzer analyzer;
	private Nifty nifty;
	private String currentPath = "./analyzerData";
	private Element errorPopup;
	private String analyzerFileName = "";
	
	public AnalyzerFileSelectionGUIController(DriveAnalyzer analyzer, Nifty nifty)
	{
		this.analyzer = analyzer;
		this.nifty = nifty;
	}
	
	
	// happens before GUI moves in
	@Override
    public void bind(Nifty nifty, Screen screen)
	{
		updateListBox();
	}

	
	// happens after GUI moved in
	@Override
    public void onStartScreen() 
	{
		
    }

	
	@Override
    public void onEndScreen() 
	{
		analyzer.analyzerFilePath = analyzerFileName;
		analyzer.simpleInitAnalyzerFile();
	}
	
	
    public void clickStartButton() 
    {
    	analyzerFileName = getTextFromTextfield("analyzerFileTextfield");
    	File analyzerFile = new File(analyzerFileName);
    	if(analyzerFile.isFile() && analyzer.isValidAnalyzerFile(analyzerFile))
    	{
    		analyzer.closeAnalyzerFileSelectionGUI();
    	}
    	else
    	{
    		// show error message when invalid analyzer file selected
    		errorPopup = nifty.createPopup("errorPopup");
    		nifty.showPopup(nifty.getCurrentScreen(), errorPopup.getId(), null);
    	}
    }


	public void clickQuitButton()
    {
    	analyzer.stop();
    }
    
    
	public void clickCloseButton()
	{
		nifty.closePopup(errorPopup.getId());
	}
	
	
    public void setTextToElement(String element, String text) 
    {
    	getElementByName(element).getRenderer(TextRenderer.class).setText(text);
    }
    
    
    public Element getElementByName(String element)
    {
    	return nifty.getCurrentScreen().findElementByName(element);
    }
    
    
    public String getTextFromTextfield(String element)
    {
    	return nifty.getCurrentScreen().findNiftyControl(element, TextField.class).getRealText();
    }
    
    
    public void setTextToTextfield(String element, String text) 
    {
    	nifty.getCurrentScreen().findNiftyControl(element, TextField.class).setText(text);
    }
    
    
	/**
	 * Fill the listbox with items. In this case with Strings.
	 */
	@SuppressWarnings("unchecked")
	public void updateListBox() 
	{
		Screen screen = nifty.getCurrentScreen();
		ListBox<TextListBoxModel> listBox = (ListBox<TextListBoxModel>) screen
				.findNiftyControl("analyzerFileListbox", ListBox.class);
		listBox.setFocusable(false);
		
		File file = new File(currentPath);
		
		if(listBox.itemCount() >= 0)
			listBox.setFocusItemByIndex(0);
		
		listBox.clear();
		
		if(file.getParent() != null)
			listBox.addItem(new TextListBoxModel(" ..", file.getParent(), Color.BLACK));

		final File[] children = file.listFiles();
	    if (children != null) 
	    {
	    	List<File> fileList = new ArrayList<File>();
	    	List<File> folderList = new ArrayList<File>();
	    	
	        for (File child : children) 
	        {
	        	if(child.isFile())
	        		fileList.add(child);
        		else
        			folderList.add(child);
	        }
	        
	        try {
        		
        		for(File singleFolder : folderList) 
        			listBox.addItem(new TextListBoxModel(" " + singleFolder.getName(), 
        					singleFolder.getCanonicalPath(),  Color.BLACK));
				
        		for(File singleFile : fileList) 
        			listBox.addItem(new TextListBoxModel(" " + singleFile.getName(), 
        					singleFile.getCanonicalPath(),  new Color("#4F94CDff")));
        		
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
	    }
	    
	    try {
	    	
			setTextToTextfield("analyzerFileTextfield", file.getCanonicalPath());
			for(int i=0;i<listBox.itemCount();i++)
				listBox.deselectItemByIndex(i);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}

	
	/**
	 * When the selection of the ListBox changes this method is called.
	 */
	@NiftyEventSubscriber(id = "analyzerFileListbox")
	public void onMyListBoxSelectionChanged(final String id,
			final ListBoxSelectionChangedEvent<TextListBoxModel> event)
	{
		List<TextListBoxModel> selection = event.getSelection();
		for (TextListBoxModel selectedItem : selection) 
		{
			currentPath = selectedItem.getPath();
			updateListBox();
		}
	}
    
	
	/**
	 * When the selection of the ListBox changes this method is called.
	 */
	@NiftyEventSubscriber(id = "analyzerFileTextfield")
	public void onanalyzerFileTextfieldChanged(final String id, final TextFieldChangedEvent event)
	{
		Button startButton = nifty.getCurrentScreen().findNiftyControl("startButton", Button.class);

		File file = new File(event.getText());
		if(file.isFile())
			startButton.setEnabled(true);
		else
			startButton.setEnabled(false);
	}

	
	@NiftyEventSubscriber(id="analyzerFileTextfield")
	public void onanalyzerFileTextfieldInputEvent(final String id, final NiftyInputEvent event) 
	{
		// update folder view, when return key was hit and focus is on driving task text field
		if (NiftyInputEvent.SubmitText.equals(event)) 
		{
			currentPath = getTextFromTextfield("analyzerFileTextfield");
			updateListBox();
		}
	}

    
}
