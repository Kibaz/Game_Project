package interfaceObjects;

import components.EntityInformation;
import entities.Entity;
import objectives.Objective;

public class Quest {
	
	private String title;
	private String description;
	private String summary;
	
	private QuestState state;

	// Create an objective
	private Objective objective;
	
	private float experience;
	
	private int levelRequirement;
	
	private int expBonus = 0;
	
	public Quest(String title, String description, String summary, Objective objective)
	{
		this.title = title;
		this.description = description;
		this.summary = summary;
		this.objective = objective;
		state = QuestState.NOT_ACCEPTED;
	}
	
	public void calculateExperience(Entity entity)
	{
		EntityInformation info = entity.getComponentByType(EntityInformation.class);
		float difficultyFactor = (float) levelRequirement / (float) info.getLevel();
		float weightingFactor = 0.2f * info.getExperienceCap();
		experience = (difficultyFactor * weightingFactor) + expBonus;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getSummary() {
		return summary;
	}

	public Objective getObjective() {
		return objective;
	}

	public float getExperience() {
		return experience;
	}

	public void setExperience(float experience) {
		this.experience = experience;
	}

	public int getLevelRequirement() {
		return levelRequirement;
	}

	public void setLevelRequirement(int levelRequirement) {
		this.levelRequirement = levelRequirement;
	}

	public int getExpBonus() {
		return expBonus;
	}

	public void setExpBonus(int expBonus) {
		this.expBonus = expBonus;
	}

	public QuestState getState() {
		return state;
	}

	public void setState(QuestState state) {
		this.state = state;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
