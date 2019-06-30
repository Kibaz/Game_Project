package components;

import rendering.Window;

public class EntityInformation extends Component {
	
	private static final float OUT_OF_COMBAT_REGEN_RATE = 1;
	
	private float timeOutOfCombat = 0;
	
	private String title;
	
	private int level;
	private int health;
	private int maxHealth;
	
	private int experienceCap;
	private int experience; // Current experience
	
	private int healthRegeneration = 5;
	
	private boolean hostile;
	
	public EntityInformation(String title, int level, int health, int maxHealth)
	{
		super("stat_list");
		this.title = title;
		this.health = health;
		this.maxHealth = maxHealth;
		this.level = level;
		init();
	}

	@Override
	protected void init() {
		experience = 0;
		experienceCap = 100;
		hostile = false;
	}

	@Override
	public void update() {
		if(entity != null)
		{
			CombatManager combatManager = entity.getComponentByType(CombatManager.class);
			if(combatManager != null)
			{
				if(!combatManager.isInCombat())
				{
					timeOutOfCombat += Window.getFrameTime();
					
					if(timeOutOfCombat > OUT_OF_COMBAT_REGEN_RATE)
					{
						if(health < maxHealth)
						{
							if(health + healthRegeneration > maxHealth)
							{
								health += (maxHealth - health); 
							}
							else
							{
								health += healthRegeneration;
							}
							
						}
						
						timeOutOfCombat = 0;
					}
				}
			}
			
			ProgressBar progressBar = entity.getComponentByType(ProgressBar.class);
			if(progressBar != null)
			{
				progressBar.setProgress(experience);
				
				if(experience >= experienceCap)
				{
					int excess = experience - experienceCap;
					experience = excess;
					experienceCap = (experienceCap * 2) + (experienceCap / 4 );
					progressBar.setCapacity(experienceCap);
					progressBar.setProgress(experience);
					level++;
				}
			}
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getExperience() {
		return experience;
	}
	
	public int getExperienceCap()
	{
		return experienceCap;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHostile() {
		return hostile;
	}

	public void setHostile(boolean hostile) {
		this.hostile = hostile;
	}
	
	
	
	
	
	

}
