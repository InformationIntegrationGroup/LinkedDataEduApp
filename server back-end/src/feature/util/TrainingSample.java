package feature.util;

import linkedData.LinkedDataConnection;
import feature.*;

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
