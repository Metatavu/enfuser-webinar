package fi.metatavu.enfuserwebinar;

import java.io.IOException;

import ucar.ma2.ArrayFloat;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * Example class for describing how latitude & longitude variables work on Enfuser NetCDF files
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class LatLonVariables implements AutoCloseable {
  
  /**
   * Private field for storing the NetCDF file object
   */
  private NetcdfFile netcdfFile;
 
  /**
   * Constructor for example class
   * 
   * @param location NetCDF file location
   * @throws IOException thrown when file opening fails
   */
  public LatLonVariables(String location) throws IOException {
    netcdfFile = NetcdfFile.open(location);
  }

  /**
   * Main method
   * 
   * @param args program arguments
   * @throws Exception thrown when reading fails
   */
  public static void main(String[] args) throws Exception {
    int latIndex = 8;
    int lonIndex = 4;
    
    try (LatLonVariables example = new LatLonVariables("file.nc")) {
      float lat = example.getLat(latIndex);
      float lon = example.getLon(lonIndex);
      
      System.out.println(String.format("Latitude index %d is %f, Longitude index %d is %f", latIndex, lat, lonIndex, lon));
    }
  }
  
  /**
   * Returns latitude value from the NetCDF file
   * 
   * @param index latitude index
   * @return latitude value from the NetCDF file
   * @throws Exception thrown when lat resolving fails
   */
  public float getLat(int index) throws Exception {
    // Retrieve variable from the file
    Variable latVariable = getVariable("lat");
    
    // Read values as an array
    ArrayFloat.D1 latValues = getFloatArray(latVariable);
    
    // Read value from specified index
    return latValues.get(index);
  }
  
  /**
   * Returns longitude value from the NetCDF file
   * 
   * @param index longitude index
   * @return longitude value from the NetCDF file
   * @throws Exception thrown when longitude resolving fails
   */
  public float getLon(int index) throws Exception {
    // Retrieve variable from the file
    Variable lonVariable = getVariable("lon");
    
    // Read values as an array
    ArrayFloat.D1 lonValues = getFloatArray(lonVariable);
    
    // Read value from specified index
    return lonValues.get(index);
  }

  /**
   * Reads float array from file
   * 
   * @return float array
   * @throws Exception thrown when array reading fails
   */
  public ArrayFloat.D1 getFloatArray(Variable variable) throws Exception {
    return (ArrayFloat.D1) variable.read(null, variable.getShape());
  }
  
  /**
   * AutoCloseable close method. Closes the NetCDF file
   */
  @Override
  public void close() throws Exception {
    this.netcdfFile.close();
  }
  
  /**
   * Finds variable by name
   * 
   * @param variableName variable name
   * @return variable or null if not found
   */
  private Variable getVariable(String variableName) {
    return netcdfFile.findVariable(variableName);
  }

}
