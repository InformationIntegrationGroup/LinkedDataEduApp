package edu.isi.serverbackend.feature.util;

import edu.isi.serverbackend.feature.DifferentOccupationFeature;
import edu.isi.serverbackend.feature.EitherNotPlaceFeature;
import edu.isi.serverbackend.linkedData.*;

public class Sample {
	protected LinkedDataConnection link;
	protected double rarity;
	protected double extensionImporatance;
	protected float eitherNotPlace;
	protected float differentOccupation;
	protected double smallPlace;
	double interestingness;
	
	public Sample(LinkedDataConnection link){
		this.link = link;
		this.rarity = 0;
		this.extensionImporatance = 0;
		this.eitherNotPlace = 0;
		this.differentOccupation = 0;
		this.smallPlace = 0;
		this.interestingness = 0;
		
	}
	
	public Sample(LinkedDataConnection link, double interestingness){
		this.link = link;
		this.rarity = 0;
		this.eitherNotPlace = 0;
		this.differentOccupation = 0;
		this.smallPlace = 0;
		this.extensionImporatance = 0;
		this.interestingness = interestingness;
		
	}
	
	public void evalutateFeature(){
		//rarity = RarityFeature.calculateRarity(link);
		eitherNotPlace = EitherNotPlaceFeature.isEitherNotPlace(link);
		differentOccupation = DifferentOccupationFeature.isDifferentOccupation(link);
		//smallPlace = SmallPlaceFeature.calculateSmallPlace(link);
	}

	public LinkedDataConnection getLink() {
		return link;
	}

	public void setLink(LinkedDataConnection link) {
		this.link = link;
	}

	public double getRarity() {
		return rarity;
	}

	public void setRarity(double rarity) {
		this.rarity = rarity;
	}

	public float getEitherNotPlace() {
		return eitherNotPlace;
	}

	public void setEitherNotPlace(float eitherNotPlace) {
		this.eitherNotPlace = eitherNotPlace;
	}

	public float getDifferentOccupation() {
		return differentOccupation;
	}

	public void setDifferentOccupation(float differentOccupation) {
		this.differentOccupation = differentOccupation;
	}

	public double getSmallPlace() {
		return smallPlace;
	}

	public void setSmallPlace(double smallPlace) {
		this.smallPlace = smallPlace;
	}
	
	public double getExtensionImportance(){
		return this.extensionImporatance;
	}
	
	public void setExtensionImportance(double imp){
		this.extensionImporatance = imp;
	}
	public double getInterestingness() {
		return interestingness;
	}

	public void setInterestingness(double interestingness) {
		this.interestingness = interestingness;
	}

}
