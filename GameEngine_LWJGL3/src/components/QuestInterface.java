package components;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;
import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUITexture;
import interfaceObjects.Quest;
import interfaceObjects.QuestState;

public class QuestInterface extends Component{
	
	private Quest quest;
	
	private GUIText titleDisplay;
	private GUIText descriptionDisplay;
	private GUIText summaryDisplay;
	
	private GUIText acceptButtonText;
	private GUIText declineButtonText;
	private GUIText completeButtonText;
	
	private GUITexture questLogTexture;
	private GUITexture acceptButtonTexture;
	private GUITexture declineButtonTexture;
	
	private GUITexture completeButtonTexture;
	
	private GUI questLog;
	private GUI acceptButton;
	private GUI declineButton;
	private GUI completeButton;
	
	private boolean visible;
	
	public QuestInterface(Quest quest)
	{
		super("quest_log");
		this.quest = quest;
		init();
	}

	@Override
	protected void init() {
		visible = false;
		float fontSize = 0.6f;
		FontStyle font = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		
		// Load GUI Textures
		questLogTexture = new GUITexture(loader.loadTexture("res/ent_profile.png"),new Vector2f(-0.625f,0),new Vector2f(0.35f,0.8f));
		acceptButtonTexture = new GUITexture(loader.loadTexture("res/test_button.png"),new Vector2f(-0.55f,-0.625f), new Vector2f(0.05f,0.05f));
		declineButtonTexture = new GUITexture(loader.loadTexture("res/test_button.png"),new Vector2f(-0.4f,-0.625f), new Vector2f(0.05f,0.05f));
		completeButtonTexture = new GUITexture(loader.loadTexture("res/test_button.png"),new Vector2f(-0.4f,-0.625f), new Vector2f(0.06f,0.05f));
		
		// Construct GUIs using textures
		questLog = new GUI(questLogTexture);
		acceptButton = new GUI(acceptButtonTexture);
		acceptButton.setClickable(true);
		declineButton = new GUI(declineButtonTexture);
		declineButton.setClickable(true);
		completeButton = new GUI(completeButtonTexture);
		completeButton.setClickable(true);
		
		// Create textual displays
		titleDisplay = new GUIText(quest.getTitle(),fontSize,font,new Vector2f(0.05f,0.15f),1,false);
		titleDisplay.setColour(1, 0.84f ,0);
		descriptionDisplay = new GUIText(quest.getDescription(),fontSize,font,new Vector2f(0.05f,0.2f),0.275f,false);
		descriptionDisplay.setColour(1, 1, 1);
		summaryDisplay = new GUIText(quest.getSummary(),fontSize,font,new Vector2f(0.05f,0.6f),0.275f,false);
		summaryDisplay.setColour(1, 1, 1);
		
		acceptButtonText = new GUIText("Accept", 0.5f, font, new Vector2f(-0.275f,0.8f),1,true);
		acceptButtonText.setColour(1, 1, 1);
		declineButtonText = new GUIText("Decline", 0.5f, font, new Vector2f(-0.2f,0.8f),1,true);
		declineButtonText.setColour(1, 1, 1);
		completeButtonText = new GUIText("Complete", 0.5f, font, new Vector2f(-0.2f,0.8f),1,true);
		completeButtonText.setColour(1, 1, 1);
		
	}

	@Override
	public void update() {
		if(acceptButton.isClicked())
		{
			QuestTracker.addQuest(quest);
			quest.setState(QuestState.ACCEPTED);
			visible = false;
		}
		
		if(declineButton.isClicked())
		{
			quest.setState(QuestState.DECLINED);
			visible = false;
		}
		
		if(completeButton.isClicked())
		{
			quest.setState(QuestState.COMPLETED);
			visible = false;
		}

		if(visible && (quest.getState() == QuestState.NOT_ACCEPTED 
				|| quest.getState() == QuestState.DECLINED 
				|| quest.getState() == QuestState.FAILED))
		{
			questLog.setVisible(true);
			acceptButton.setVisible(true);
			declineButton.setVisible(true);
			TextController.loadText(titleDisplay);
			TextController.loadText(descriptionDisplay);
			TextController.loadText(summaryDisplay);
			TextController.loadText(acceptButtonText);
			TextController.loadText(declineButtonText);
		}
		
		if(visible && (quest.getState() == QuestState.OBJECTIVE_COMPLETE))
		{
			questLog.setVisible(true);
			completeButton.setVisible(true);
			TextController.loadText(completeButtonText);
		}
		
		if(visible && (quest.getState() == QuestState.ACCEPTED))
		{
			questLog.setVisible(true);
			
		}
		
		
		if(!visible)
		{
			questLog.setVisible(false);
			acceptButton.setVisible(false);
			declineButton.setVisible(false);
			completeButton.setVisible(false);
			TextController.removeText(titleDisplay);
			TextController.removeText(descriptionDisplay);
			TextController.removeText(summaryDisplay);
			TextController.removeText(acceptButtonText);
			TextController.removeText(declineButtonText);
			TextController.removeText(completeButtonText);
		}
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	public Quest getQuest() {
		return quest;
	}

	public GUIText getTitleDisplay() {
		return titleDisplay;
	}

	public GUIText getDescriptionDisplay() {
		return descriptionDisplay;
	}

	public GUIText getSummaryDisplay() {
		return summaryDisplay;
	}

	public GUIText getAcceptButtonText() {
		return acceptButtonText;
	}

	public GUIText getDeclineButtonText() {
		return declineButtonText;
	}

	public GUITexture getQuestLogTexture() {
		return questLogTexture;
	}

	public GUITexture getAcceptButtonTexture() {
		return acceptButtonTexture;
	}

	public GUITexture getDeclineButtonTexture() {
		return declineButtonTexture;
	}

	public GUI getQuestLog() {
		return questLog;
	}

	public GUI getAcceptButton() {
		return acceptButton;
	}

	public GUI getDeclineButton() {
		return declineButton;
	}
	
	public GUI getCompleteButton() {
		return completeButton;
	}

	public boolean isVisible()
	{
		return visible;
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	

}
