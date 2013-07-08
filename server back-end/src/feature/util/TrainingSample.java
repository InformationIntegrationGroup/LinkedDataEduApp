package feature.util;

import linkedData.LinkedDataConnection;
import feature.*;

public class TrainingSample {
	LinkedDataConnection link;
	float rarity;
	float eitherNotPlace;
	float differentOccupation;
	float smallPlace;
	float interestingness;
	public TrainingSample(LinkedDataConnection link, float interestingness){
		this.link = link;
		this.rarity = -1;
		this.eitherNotPlace = -1;
		this.differentOccupation = -1;
		this.smallPlace = -1;
		this.interestingness = interestingness;
	}
	
	public void evalutateFeature(){
		rarity = RarityFeature.calculateRarity(link);
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

	public float getRarity() {
		return rarity;
	}

	public void setRarity(float rarity) {
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

	public float getSmallPlace() {
		return smallPlace;
	}

	public void setSmallPlace(float smallPlace) {
		this.smallPlace = smallPlace;
	}

	public float getInterestingness() {
		return interestingness;
	}

	public void setInterestingness(float interestingness) {
		this.interestingness = interestingness;
	}
	
	
}
