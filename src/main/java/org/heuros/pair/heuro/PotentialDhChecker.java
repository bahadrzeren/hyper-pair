package org.heuros.pair.heuro;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.repo.DataRepository;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;

public class PotentialDhChecker implements Callable<Boolean> {

	private static Logger logger = Logger.getLogger(PotentialDhChecker.class);

	private int hbNdx = -1;
	private LocalDateTime coverPeriodEndExc = null;

	public PotentialDhChecker(int hbNdx, LocalDateTime coverPeriodEndExc) {
		this.hbNdx = hbNdx;
		this.coverPeriodEndExc = coverPeriodEndExc;
	}

	private List<Leg> legs = null;
	private List<Duty> duties = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	public PotentialDhChecker setLegRepository(DataRepository<Leg> legRepository) {
		this.legs = legRepository.getModels();
		return this;
	}

	public PotentialDhChecker setDutyRepository(DataRepository<Duty> dutyRepository) {
		this.duties = dutyRepository.getModels();
		return this;
	}

	public PotentialDhChecker setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

	@Override
	public Boolean call() {

		logger.info("Potential Dh check is started!");

		for (int li = 0; li < this.legs.size(); li++) {
    		Leg l = this.legs.get(li);

    		if (l.isCover()
    				&& l.hasPair(this.hbNdx)
            		&& l.getSobt().isBefore(coverPeriodEndExc)) {

	    		int[] legAssociationVector = new int[this.legs.size()];
//	    		int[] legAssociationVectorWoDh = new int[this.legs.size()];
	    		int numOfDuties = 0;
//	    		int numOfDutiesWoDh = 0;
	    		int maxNumOfAssociations = 0;
//	    		int maxNumOfAssociationsWoDh = 0;

    			Duty[] ds = this.dutyIndexByLegNdx.getArray(l.getNdx());

            	if ((ds != null)
            			&& (ds.length > 0)) {

            		for (Duty d: ds) {

            			if (d.isValid(this.hbNdx)
            					&& d.hasPairing(this.hbNdx)) {
				    		/*
				    		 * Legs association identification!
				    		 */
				    		numOfDuties++;
//				    		if (d.getNumOfLegsPassive() == 0)
//				    			numOfDutiesWoDh++;
				    		for (int cli = 0; cli < d.getLegs().size(); cli++) {
				    			Leg cl = d.getLegs().get(cli);
				    			if (cl.isCover()
				    					&& (cl.getNdx() != l.getNdx())) {
				    				legAssociationVector[cl.getNdx()]++;
//					    			if (d.getNumOfLegsPassive() == 0)
//					    				legAssociationVectorWoDh[cl.getNdx()]++;
					    			if (maxNumOfAssociations < legAssociationVector[cl.getNdx()])
					    				maxNumOfAssociations = legAssociationVector[cl.getNdx()];
//					    			if (maxNumOfAssociationsWoDh < legAssociationVectorWoDh[cl.getNdx()])
//					    				maxNumOfAssociationsWoDh = legAssociationVectorWoDh[cl.getNdx()];
				    			}
				    		}
            			}
            		}

            		/*
            		 * Critical legs & duties identification.
            		 */
//            		boolean[] dsFlagVector = new boolean[legAssociationVector.length];
//            		this.enumerateCriticalLegs(l, ds, legAssociationVector, dsFlagVector, ds.length);

            		if (numOfDuties == maxNumOfAssociations) {
			    		for (int ali = 0; ali < legAssociationVector.length; ali++) {
			    			if (legAssociationVector[ali] == maxNumOfAssociations) {
			    				Duty[] aDuties = dutyIndexByLegNdx.getArray(ali);
			    				for (Duty aDuty : aDuties) {
			    					if (aDuty.isValid(hbNdx)
			    							&& aDuty.hasPairing(hbNdx)) {
										boolean hasCriticalLeg = false;
										for (int aldi = 0; aldi < aDuty.getLegs().size(); aldi++) {
											if (aDuty.getLegs().get(aldi).getNdx() == l.getNdx()) {
												hasCriticalLeg = true;
												break;
											}
										}
										if (!hasCriticalLeg) {
											aDuty.setTotalNumOfPotentialIndirectDhLegs(1);
											l.setPotentialDhLevel(1);
										}
			    					}
								}
			    			}
			    		}
		    		}
//            		if (numOfDutiesWoDh == maxNumOfAssociationsWoDh) {
//			    		for (int ali = 0; ali < legAssociationVectorWoDh.length; ali++) {
//			    			if (legAssociationVectorWoDh[ali] == maxNumOfAssociationsWoDh) {
//			    				Duty[] aDuties = dutyIndexByLegNdx.getArray(ali);
//			    				for (Duty aDuty : aDuties) {
//			    					if (aDuty.isValid(hbNdx)
//			    							&& aDuty.hasPairing(hbNdx)
//			    							/*
//			    							 * Critical duties are not necessarily DH free. 
//			    							 */
////			    							&& (aDuty.getNumOfLegsPassive() == 0)
//			    							) {
//										boolean hasCriticalLeg = false;
//										for (int aldi = 0; aldi < aDuty.getLegs().size(); aldi++) {
//											if (aDuty.getLegs().get(aldi).getNdx() == l.getNdx()) {
//												hasCriticalLeg = true;
//												break;
//											}
//										}
//										if (!hasCriticalLeg) {
//											aDuty.setCriticalLegWoDh(l);
//											l.setCriticalWoDh(true);
//										}
//			    					}
//								}
//			    			}
//			    		}
//		    		}
    			}
//            	if (l.isCritical())
//            		logger.info("Critical leg: " + l);
	    	}
		}

//		for (Duty duty : this.duties) {
//			if (duty.isValid(hbNdx)
//					&& duty.hasPairing(hbNdx)) {
//				logger.info(duty.getNdx() + " -- " + duty.getTotalNumOfPotentialIndirectDhLegs());
//			}
//		}
//		for (Leg leg : this.legs) {
//    		if (leg.isCover()
//    				&& leg.hasPair(this.hbNdx)
//            		&& leg.getSobt().isBefore(coverPeriodEndExc)) {
//				logger.info(leg.toString() + " -- " + leg.getPotentialDhLevel());
//			}
//		}

		logger.info("Potential Dh check finished!");

		return true;
	}

//	private boolean enumerateCriticalLegs(Leg l, Duty[] ds, int[] legAsscVector, boolean[] dsFlagVector, int numOfRestOfTheDuties) {
//
//		boolean res = false;
//
//		while (true) {
//			int maxNumOfExistence = 0;
//			int legNdx = -1;
//    		for (int ali = 0; ali < legAsscVector.length; ali++) {
//    			if (legAsscVector[ali] > maxNumOfExistence) {
//    				maxNumOfExistence = legAsscVector[ali];
//    				legNdx = ali;
//    			}
//			}
//    		legAsscVector[legNdx] = 0;
//
//    		if (legNdx >= 0) {
//
//        		int[] legAsscs = new int[legAsscVector.length];
//        		boolean[] dsFlags = dsFlagVector.clone();
//
//        		int numOfRestOfTheDuties = 0;
//        		for (int di = 0; di < ds.length; di++) {
//					Duty d = ds[di];
//					if (d.isValid(hbNdx)
//							&& d.hasPairing(hbNdx)
//							&& (!dsFlags[di])) {
//						boolean hasLeg = false;
//						for (int aldi = 0; aldi < d.getLegs().size(); aldi++) {
//			    			Leg cl = d.getLegs().get(aldi);
//			    			if (cl.getNdx() == legNdx) {
//								hasLeg = true;
//								break;
//							}
//						}
//						if (hasLeg) {
//							dsFlags[di] = true;
//						} else {
//							numOfRestOfTheDuties++;
//							for (int aldi = 0; aldi < d.getLegs().size(); aldi++) {
//				    			Leg cl = d.getLegs().get(aldi);
//				    			if (cl.isCover()
//				    					&& (cl.getNdx() != l.getNdx()))
//				    				legAsscs[cl.getNdx()]++;
//							}
//						}
//					}
//				}
//
//        		
//    		} else
//    			break;
//		}
//		return res;
//	}
}
