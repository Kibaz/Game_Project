package components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entities.Entity;
import interfaceObjects.Quest;
import interfaceObjects.QuestState;
import objectives.Enumeration;
import objectives.Objective;

public class QuestTracker{
	
	private static List<Quest> quests;
	
	public static void init()
	{
		quests = new ArrayList<>();
	}
	
	public static void addQuest(Quest quest)
	{
		quests.add(quest);
	}
	
	public static void removeQuest(Quest quest)
	{
		quests.remove(quest);
	}
	
	public static void update(Entity player)
	{
		EntityInformation info = player.getComponentByType(EntityInformation.class);
		Iterator<Quest> iterator = quests.iterator();
		while(iterator.hasNext())
		{
			Quest quest = iterator.next();
			Objective objective = quest.getObjective();
			if(objective.isCompleted() && quest.getState() != QuestState.COMPLETED)
			{
				quest.setState(QuestState.OBJECTIVE_COMPLETE);
			}
			
			if(quest.getState() == QuestState.COMPLETED)
			{
				quest.calculateExperience(player);
				info.setExperience(info.getExperience() + (int) quest.getExperience());
				iterator.remove();
			}
		}

		
	}
	
	public static void notifyTracker(EntityInformation info)
	{
		for(Quest quest: quests)
		{
			if(!quest.getObjective().isCompleted())
			{
				if(quest.getObjective() instanceof Enumeration)
				{
					Enumeration enumObjective = (Enumeration) quest.getObjective();
					if(enumObjective.getAssocEntity().equals(info.getTitle()))
					{
						enumObjective.update();
					}
				}
			}
		}
	}

}
