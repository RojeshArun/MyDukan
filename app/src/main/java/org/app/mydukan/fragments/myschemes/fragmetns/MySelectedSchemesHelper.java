package org.app.mydukan.fragments.myschemes.fragmetns;

import org.app.mydukan.data.Scheme;
import org.app.mydukan.data.SchemeRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rojesharunkumar on 14/11/17.
 */

public class MySelectedSchemesHelper {

    private ArrayList<ArrayList<Scheme>> mAllProductsList;
    private List<List<Scheme>> mySelectedList;
    private Scheme myCurrentBrand;
    private ArrayList<SchemeRecord> recordList;

    private static MySelectedSchemesHelper instance;

    private MySelectedSchemesHelper() {
        mySelectedList = new ArrayList<>();
    }


    public static MySelectedSchemesHelper getInstance() {
        if (instance == null) {
            instance = new MySelectedSchemesHelper();
        }
        return instance;
    }

/*
    public List<Scheme> getList() {
        return mySelectedList;
    }
*/


    public void addBrand(Scheme selectedBrand) {

        if (!checkIsMyBrandAdded(selectedBrand)) {
            myCurrentBrand = selectedBrand;
            // mySelectedList.add(selectedBrand);
        }
    }

    private boolean checkIsMyBrandAdded(Scheme selectedBrand) {
        if (mySelectedList != null && selectedBrand != null) {
            for (int i = 0; i < mySelectedList.size(); i++) {
                /*if (mySelectedList.get(i).getSchemeId().equals(selectedBrand.getSchemeId())) {
                    return true;
                }*/
            }
            return false;
        }
        return false;
    }


    private int getPosition(String brandId) {
        if (mySelectedList != null) {
            for (int i = 0; i < mySelectedList.size(); i++) {
              /*  if (brandId.equals(mySelectedList.get(i).getSchemeId()))
                    return i;*/
            }
        }
        return 0;
    }

    public Scheme getCurrentBrand() {
        return myCurrentBrand;
    }

    public void reset() {
        mySelectedList = new ArrayList<>();
        myCurrentBrand = null;
    }

/*
    public void setList(List<Scheme> mySelectedList) {
        this.mySelectedList = mySelectedList;
    }
*/

    public void setAllProductsList(ArrayList<ArrayList<Scheme>> mySelectedList) {
        this.mAllProductsList = mySelectedList;
    }

    public List<ArrayList<Scheme>> getAllProductList() {
        return mAllProductsList;
    }


    public List<List<Scheme>> getAllSelectedSchemesList() {
        return mySelectedList;
    }

    public void updateMySelectedList(ArrayList<SchemeRecord> mRecordList) {
//TODO
        // Set already selected list
        if (mySelectedList == null) {
            mySelectedList = new ArrayList<>();
        } else {
            mySelectedList.clear();
        }
        List<Scheme> schemesList;

        for (int i = 0; i < mRecordList.size(); i++) {
            for (int j = 0; j < mAllProductsList.size(); j++) {
                schemesList = mAllProductsList.get(j);
                Scheme scheme;
                List<Scheme> mySchemesList, selectedSchemesList = new ArrayList<>();
                for (int k = 0; k < schemesList.size(); k++) {
                    //TODO
                    if (mRecordList.get(i).getSchemeinfo().getId().equalsIgnoreCase(schemesList.get(k).getSchemeId())) {
                        scheme = schemesList.get(k);
                        selectedSchemesList.add(scheme);
                    }
                }
                if (selectedSchemesList != null & selectedSchemesList.size() > 0) {
                    mySelectedList.add(selectedSchemesList);
                }

            }
        }


    }


    public void updateAt(ArrayList<Scheme> mSchemeList, int pos) {
        mAllProductsList.set(pos, mSchemeList);

        // Checking for the category
        for (int i = 0; i < mySelectedList.size(); i++) {
            if (mySelectedList.get(i).get(0).getCategory().equals(mSchemeList.get(0))) {
                mySelectedList.set(i, mSchemeList);
                break;
            }
        }
    }

    public ArrayList<SchemeRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<SchemeRecord> recordList) {
        this.recordList = recordList;
    }

    public void addSchemeAt(int brandPos, int pos, boolean isChecked) {
        //TODO
        // Check that item is present or not, if present
        if(isChecked){
            // Add
            Scheme scheme =mAllProductsList.get(brandPos).get(pos);
          //  scheme.setHasEnrolled(isChecked);
            mAllProductsList.get(brandPos).get(pos).setHasEnrolled(isChecked);


           // updateAt(mAllProductsList.get(brandPos),pos);

        }else{
            // Remove if present


        }

    }
}
