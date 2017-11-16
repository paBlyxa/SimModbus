package com.we.simModbus.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.simModbus.model.Tag;
import com.we.simModbus.model.TimeCyclic;

import javafx.util.Duration;

public class TagScheduleService implements TagScheduledHandler {
//Нужно как то в этом классе удалить уже созданные сервисы, при отключении связи
	private final static Logger logger = LoggerFactory.getLogger(TagScheduleService.class);

	private final Map<TimeCyclic, TagScheduleServiceThread> serviceMap;
	private TagScheduleServiceThreadFactory tagScheduleServiceThreadFactory;
	
	public TagScheduleService() {
		logger.debug("Create new TagScheduleService");
		serviceMap = new HashMap<TimeCyclic, TagScheduleServiceThread>();
	}

	public void setTagScheduleServiceThreadFactory(TagScheduleServiceThreadFactory tagScheduleServiceThreadFactory){
		this.tagScheduleServiceThreadFactory = tagScheduleServiceThreadFactory;
	}
	
	/**
	 * Add tag to cyclic change set
	 * 
	 * @param tag
	 * @param unit
	 */
	@Override
	public void addToSchedule(Tag tag, TimeCyclic unit) {
		logger.debug("Add new tag {} to shedule {}", tag.getName(), unit);
		TagScheduleServiceThread serviceThread = serviceMap.get(unit);
		if (serviceThread == null) {
			logger.debug("Create new ScheduleServiceThread");
			serviceThread = tagScheduleServiceThreadFactory.newInstance();
			serviceThread.add(tag);
			serviceMap.put(unit, serviceThread);
			serviceThread.setPeriod(Duration.millis(unit.getDelay()));
			serviceThread.start();
		} else {
			if (serviceThread.isStopped()){
				logger.debug("Service thread {} is stoped", unit);
				serviceThread.add(tag);
				serviceThread.restart();
			} else {
				serviceThread.add(tag);
			}
		}
		logger.debug("serviceThread.isRunning {}, serviceThread.isStopped {}", serviceThread.isRunning(), serviceThread.isStopped());
		

	}

	/**
	 * Remove tag from cyclic change set
	 * 
	 * @param tag
	 */
	@Override
	public void removeFromSchedule(Tag tag) {
		logger.debug("Remove tag {} from shedule", tag.getName());
		for (TagScheduleServiceThread changeService : serviceMap.values()) {
			if (changeService.remove(tag)) {
				if (changeService.isEmpty()){
					logger.debug("Stop service");
					changeService.stop();
				}
				break;
			}
		}
	}

	/**
	 * Return true if this service contains tag
	 * 
	 * @param tag
	 * @return true if this service contains tag
	 */
	@Override
	public boolean isInSchedule(Tag tag) {
		for (TagScheduleServiceThread changeService : serviceMap.values()) {
			if (changeService.contains(tag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Stop all executions.
	 */
	public void stop(){
		for (Entry<TimeCyclic, TagScheduleServiceThread> changeService : serviceMap.entrySet()) {
			logger.debug("Stop scheduled service {}", changeService.getKey());
			boolean result = changeService.getValue().stop();
			logger.debug("changeService.cancel {}, changeService.getValue().isRunning {}, changeService.getValue().isStopped {}", result, changeService.getValue().isRunning(), changeService.getValue().isStopped());
			
		}
	}
	
	/**
	 * Stop and delete all scheduleSerivces
	 */
	public void reset(){
		stop();
		serviceMap.clear();
	}

}
