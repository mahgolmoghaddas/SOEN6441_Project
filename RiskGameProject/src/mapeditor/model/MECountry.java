package mapeditor.model;


import java.util.LinkedList;

/**
 * country class
 */
public class MECountry{

    private String countryName;
    private LinkedList<String> neighbor = new LinkedList<String>();

    /**
     * set country name
     * @param newCountryName
     */
    public void setCountryName(String newCountryName){
        this.countryName = newCountryName;
    }

    /**
     * set neighbor name
     * @param newNeighbor
     */
    public void setNeighbor(String newNeighbor){
        neighbor.offer(newNeighbor);
    }

    /**
     * delete neighbor
     * @param oldNeighbor
     */
    public void deleteNeighbor(String oldNeighbor){
        if(neighbor.indexOf(oldNeighbor)!=-1){
            neighbor.remove(oldNeighbor);
        }
    }

    /**
     * get country name
     * @return country name
     */
    public String getCountryName(){return countryName;}

    /**
     * get all neighbors name for example"[A, B, C]"
     * @return neighbors name
     */
    public String getNeighbor(){return neighbor.toString();}

}