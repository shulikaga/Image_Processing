
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * PROVIDE USER_NAME, PASSWORD, SCHEMA_NAME (example 'public'),
 * AUTHOR_SOURCE_LOCATION (example "/Users/mrm/Documents/Studium/Informatik/16
 * WS Tuning von
 * Datenbankensystemen/dbt_projects/assignments/assignment01/data/auth.tsv") and
 * PUBLICATIONS_SOURCE_LOCATION as arguments
 *
 * prerequisites: a postgresql database named "dbtuning_ws2016" on localhost
 * port 10000
 *
 */

public class ImagesDatabase {
    DatabaseConnector db;
        String[][] resultSet;
    ImagesDatabase(String patientMappingFileLocation, ArrayList<Images.Image> images, int vectorsSize){
        
        db = new DatabaseConnector("localhost", "5433", "postgres", "public", "TtlshIwwya10", "postgres" );
        
        try (Connection con = db.getConnection()) {
            
             createPatientsTable(con);
             createImagesTable(con, vectorsSize);
             
            populatePatientsTableUseCopy(con, patientMappingFileLocation);   
            populateImagesTable(con, images);
             
            createCancerClassesTable(con, vectorsSize);

            createClassifiedImagesTable(con, vectorsSize);            
            populateClassifiedImagesTable(con);
             
            createImgPatClassifiedTable(con);
            
             buildClassifiedImages(con, vectorsSize,712);
                          
                         
             
         } catch (Exception e) {
        }
    }
    
  
    
    private static void dropTable(Connection con, String tableName) {
        try (Statement stmt = con.createStatement()) {
            String qry = "DROP table if exists " + tableName;
            stmt.executeUpdate(qry);
        } catch (Exception e) {
            System.err.println("Query  dropTable was not successful.");
        }
    }
    
    private void createPatientsTable(Connection con){
         dropTable(con,"Patients");
         
         try (Statement stmt = con.createStatement()) {
           
             String qry = "CREATE table if not exists Patients ("
                    + "imageId varchar(49), "
                    + "patientId Integer, "
                    + "primary key(imageId))";
            
             stmt.executeUpdate(qry);
      
         } catch (Exception e) {
            System.err.println("Query createPatientsTable was not successful.");
        }
    }
     
    private void createImagesTable(Connection con, int vectorSize){
         dropTable(con,"Images");
         
         try (Statement stmt = con.createStatement()) {
          
             String qry = "CREATE table if not exists Images ("
                    + "imageId varchar(49), "
                    + "imageVectors2DArray float8[12]["+vectorSize+"], "
                    + "primary key(imageId))";
          
             stmt.executeUpdate(qry);
      
         } catch (Exception e) {
            System.err.println("Query createImagesTable was not successful.");
        }
    }
    
    private void createCancerClassesTable(Connection con, int vectorsSize) throws SQLException{
         dropTable(con,"CancerClasses");
         
          try (Statement stmt = con.createStatement();) {
           
            String qry =  "CREATE table if not exists CancerClasses "
                          + "AS SELECT DISTINCT Images.imageId, patientId, imageVectors2DArray, 0 AS Class "
            + "FROM Images, Patients "
            + "WHERE Images.imageId = Patients.imageId";
          
             stmt.executeUpdate(qry);
             
          } catch (Exception e) {
            System.err.println("Query createCancerClassesTable was not successful.");
        }
    }
     
     
    private static void populatePatientsTableUseCopy(Connection con, String source) throws IOException {
        FileReader fileReader = new FileReader(source);
       
        try {
            CopyManager cm = new CopyManager((BaseConnection) con);
            cm.copyIn("COPY Patients(imageId, patientId) FROM STDIN WITH DELIMITER ';'", fileReader);
                    
             try (Statement stmt = con.createStatement();) {
             
             }
            
        } catch (SQLException e) {
            System.err.println("Query was not successful.");
        }
    }
    
    private void populateImagesTable(Connection con, ArrayList<Images.Image> images) {
        Iterator imagesIt = images.iterator();
        String query;
        
        while(imagesIt.hasNext()){
            Images.Image image = (Images.Image)imagesIt.next();
            
        
          try (Statement statement = con.createStatement()) {
              
              String imageVectorsArray = "";
              
              for(int i = 0;i<image.vectors.length; i++){
                     if(i == 0){
                          imageVectorsArray +="ARRAY["; 
                      }
                  
                  for(int j = 0;j<image.vectors[i].length;j++){
                      
                      if(j == 0){
                          imageVectorsArray +="["; 
                          imageVectorsArray += image.vectors[i][j]+ ", ";
                      }else if(j == image.vectors[i].length-1 && i == image.vectors.length-1){
                          imageVectorsArray += image.vectors[i][j]+ "";
                          imageVectorsArray +="]]";
                      }else if(j == image.vectors[i].length-1 && i < image.vectors.length-1){
                          imageVectorsArray += image.vectors[i][j]+ "";
                          imageVectorsArray +="],"; 
                      }else{
                          imageVectorsArray += image.vectors[i][j]+ ", ";
                      }
                    
                      
                      
                  }
              }

                query = "INSERT INTO Images VALUES('"+image.imageId+"', "+imageVectorsArray+")";
                statement.executeUpdate(query);
          
          } catch (Exception e) {
                System.err.println("Query  populateImagesTable was not successful.");
          }
        }        
                    
        

    }

    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {

            if (name.endsWith(".png")) {
                return (true);
            }
            return (false);
        }
    };
   

    private void createClassifiedImagesTable(Connection con, int vectorSize) {
      
        dropTable(con,"ClassifiedImages");
         
         try (Statement stmt = con.createStatement()) {
          
             String qry = "CREATE table if not exists ClassifiedImages ("
                    + "imageId varchar(49), "
                    + "cancerClass integer, "
                    + "pitPattern varchar(49), "
                    + "primary key(imageId))";
          
             stmt.executeUpdate(qry);
      
         } catch (Exception e) {
            System.err.println("Query createClassifiedImagesTable was not successful.");
        }
    }

    private void populateClassifiedImagesTable(Connection con) {
        String pattern1Path = "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/pit-images/Pit Pattern I/";
        String pattern2Path = "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/pit-images/Pit Pattern II/";
        String pattern3LPath = "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/pit-images/Pit Pattern III L/";
        String patterm3SPath = "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/pit-images/Pit Pattern III S/";
        String pattern4Path = "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/pit-images/Pit Pattern IV/";
        String pattern5Path = "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/pit-images/Pit Pattern V/";
        
        
        File p1 = new File(pattern1Path);
        File p2 = new File(pattern2Path);
        File p3L = new File(pattern3LPath);
        File p3S = new File(patterm3SPath);
        File p4 = new File(pattern4Path);
        File p5 = new File(pattern5Path);
        
        String query;
     try (Statement statement = con.createStatement()) {  
        
         if (p1.isDirectory()) {
            for (File file : p1.listFiles(IMAGE_FILTER)) {                               
                     query = "INSERT INTO ClassifiedImages VALUES('"+file.getName()+"', '1', 'p1')";
                     statement.executeUpdate(query);              
            }
        }
        
        if (p2.isDirectory()) {
            for (File file : p2.listFiles(IMAGE_FILTER)) {
                    query = "INSERT INTO ClassifiedImages VALUES('"+file.getName()+"', '1', 'p2')";
                    statement.executeUpdate(query);                            
            }
        }
        
        if (p3L.isDirectory()) {
            for (File file : p3L.listFiles(IMAGE_FILTER)) {
                     query = "INSERT INTO ClassifiedImages VALUES('"+file.getName()+"', '2', 'p3L')";
                     statement.executeUpdate(query);                               
            }
        }
        
        if (p3S.isDirectory()) {
            for (File file : p3S.listFiles(IMAGE_FILTER)) {
                    query = "INSERT INTO ClassifiedImages VALUES('"+file.getName()+"', '2', 'p3S')";
                    statement.executeUpdate(query);                                   
            }
        }
        
        if (p4.isDirectory()) {
            for (File file : p4.listFiles(IMAGE_FILTER)) {
                     query = "INSERT INTO ClassifiedImages VALUES('"+file.getName()+"', '2', 'p4')";
                     statement.executeUpdate(query);                               
            }
        }
        
        if (p5.isDirectory()) {
            for (File file : p5.listFiles(IMAGE_FILTER)) {
                     query = "INSERT INTO ClassifiedImages VALUES('"+file.getName()+"', '3', 'p5')";
                     statement.executeUpdate(query);                                
            }  
        }
     } catch (Exception e) {
            System.err.println("Query populateClassifiedImagesTable was not successful.");
        }
  } 

    private void createImgPatClassifiedTable(Connection con) {
        dropTable(con,"ImgPatClassified");
         
          try (Statement stmt = con.createStatement();) {
           
            String qry =  "CREATE table if not exists ImgPatClassified "
                          + "AS SELECT DISTINCT ClassifiedImages.imageId, Patients.patientId, Images.imageVectors2DArray, ClassifiedImages.cancerClass "
            + "FROM ClassifiedImages, Patients, Images "
            + "WHERE ClassifiedImages.imageId = Patients.imageId AND ClassifiedImages.imageId = Images.imageId "
            + "ORDER BY Patients.patientId";
          
             stmt.executeUpdate(qry);
             
          } catch (Exception e) {
            System.err.println("Query createImgPatClassifiedTable was not successful.");
        }
    }
  
    
 public void buildClassifiedImages(Connection con, int vectorsSize, int imagesSize ) throws IOException{
     
      resultSet = new String[imagesSize][4];
      
      try (Statement stmt = con.createStatement();) {       
            String qry =  "SELECT DISTINCT imageId, patientId, imageVectors2DArray, cancerClass "
                    + "FROM ImgPatClassified";
        
            ResultSet rs = stmt.executeQuery(qry);
            String imageId;
            String patientId;
            String cancerClass;
           
            int c = 0;
           
             while (rs.next()){             
                    imageId = rs.getString("imageId");//0
                    patientId = rs.getString("patientId");//1
                    cancerClass = rs.getString("cancerClass");//2  
                    String vectors = rs.getString("imageVectors2DArray").replace("{{", "");//3
                  
                    resultSet[c][0] = imageId;
                    resultSet[c][1] = patientId;
                    resultSet[c][2] = cancerClass;
                    resultSet[c][3] = vectors;
                            
                    c++;
                   
            }
             rs.close();
             stmt.close();
                
          } catch (Exception e) {
              e.printStackTrace();
            System.err.println("Query get buildClassifiedImages was not successful.");
          }
      
         
             
  }

    String[][] getResultSet() {
        return resultSet;
    }
    
}
