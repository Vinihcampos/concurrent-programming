package business.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import business.util.Log;
import model.Person;

public class BathroomManagerMonitor implements BathroomManager{

	private volatile Integer capacity;
	private volatile List<Person> insidePeople;
	private volatile List<Person> waitingPeople;
	private volatile Integer timeElapsed;
	private volatile Integer outsideTimeElapsed;
	
	public BathroomManagerMonitor(Integer capacity) {
		this.capacity = capacity;
		this.insidePeople = new ArrayList<>();
		this.waitingPeople = new ArrayList<>();
		this.timeElapsed = 0;
		this.outsideTimeElapsed = 0;
	}
	
	@Override
	public synchronized Boolean isEmpty() {
		return insidePeople.isEmpty();
	}

	@Override
	public synchronized Integer getTimeSleep() {
		return insidePeople.isEmpty() ? null : insidePeople.get(0).getTime();
	}

	@Override
	public synchronized void add(Person p) {
		if(insidePeople.isEmpty()) {
			insidePeople.add(p);
			Log.entrance(p, insidePeople.size(), Math.max(timeElapsed, outsideTimeElapsed));
			Collections.sort(insidePeople);
		}else {
			if(insidePeople.get(0).getGender() == p.getGender() && 
			   insidePeople.size() < capacity && waitingPeople.isEmpty()) {
				insidePeople.add(p);
				Log.entrance(p, insidePeople.size(), Math.max(timeElapsed, outsideTimeElapsed));
				Collections.sort(insidePeople);
			}else {
				waitingPeople.add(p);
			}
		}
		timeElapsed++;	
	}

	@Override
	public synchronized void remove() {
		if(!insidePeople.isEmpty()) {
			Integer time = insidePeople.get(0).getTime();
			outsideTimeElapsed += time;
			Person auxP;
			while(!insidePeople.isEmpty() && insidePeople.get(0).getTime() == time) {
				auxP = insidePeople.remove(0);
				Log.exit(auxP, insidePeople.size(),outsideTimeElapsed);					
			}
			
			for(int i = 0; i < insidePeople.size(); ++i) {
				insidePeople.get(i).setTime( insidePeople.get(i).getTime() - time );
			}
			
			if(insidePeople.size() < capacity && !waitingPeople.isEmpty()) {
				
				Boolean gender = !insidePeople.isEmpty() ? insidePeople.get(0).getGender() : waitingPeople.get(0).getGender();
				
				while(!waitingPeople.isEmpty() && 
					  waitingPeople.get(0).getGender() == gender &&
					  insidePeople.size() < capacity) {
					insidePeople.add(waitingPeople.get(0));
					auxP = waitingPeople.remove(0);
					Log.entrance(auxP, insidePeople.size(),outsideTimeElapsed);
				}
				Collections.sort(insidePeople);
			}
		}
	}
}
