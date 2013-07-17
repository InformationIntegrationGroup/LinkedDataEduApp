package edu.isi.serverbackend.feature.util;

import edu.isi.serverbackend.linkedData.LinkedDataConnection;
import edu.isi.serverbackend.feature.*;

public class TrainingSample extends Sample{
	
	float interestingness;
	public TrainingSample(LinkedDataConnection link, float interestingness){
		super(link);
		this.interestingness = interestingness;
	}
	
	public void evalutateFeature(){
		rarity = RarityFeature.calculateRarity(link);
		eitherNotPlace = EitherNotPlaceFeature.isEitherNotPlace(link);
		differentOccupation = DifferentOccupationFeature.isDifferentOccupation(link);
		//smallPlace = SmallPlaceFeature.calculateSmallPlace(link);
	}
	
}
