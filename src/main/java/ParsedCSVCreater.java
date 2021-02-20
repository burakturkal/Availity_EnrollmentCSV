import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class ParsedCSVCreater {

    public static HashMap<String, List<Enrollee>> hashMap = new HashMap<String, List<Enrollee>>();

    public static final String CSVfilePath = "/Users/admin/Desktop/AvailityHW3/TestData.csv";

    public static void main(String[] args) throws IOException {
        CSVParser csvParser = createCSVParser(CSVfilePath);
        HashMap<String, List<Enrollee>> parsedCSVFile = parseCSVFile(csvParser);
        createNewCSVFiles(parsedCSVFile);
    }

    public static CSVParser createCSVParser(String CSVFilePath) throws IOException {
        //Reader for CSV Files, current path to Temp File
        Reader reader = new FileReader(CSVFilePath);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withHeader("userId", "firstName", "lastName", "version", "insuranceCompany")
                .withIgnoreHeaderCase()
                .withTrim());
        return csvParser;
    }

    public static HashMap<String, List<Enrollee>> parseCSVFile(CSVParser csvParser) {
        for (CSVRecord csvRecord : csvParser) {
            // Using Parser to access values by the names assigned to each column
            boolean isNew = true;
            Enrollee newEnrollee = getNewEmployee(csvRecord);
            //Check map if insurance company already exists in map
            if (hashMap.containsKey(newEnrollee.getInsuranceCompany())) {
                //Loop through Enrollee List to check for existing User Id
                for (int i = 0; i < hashMap.get(newEnrollee.getInsuranceCompany()).size(); i++) {
                    if (hashMap.get(newEnrollee.getInsuranceCompany()).get(i).getUserId().equals(newEnrollee.getUserId())) {
                        //Check if new Enrollee entry has latest Version
                        if (hashMap.get(newEnrollee.getInsuranceCompany()).get(i).getVersion() < newEnrollee.getVersion()) {
                            hashMap.get(newEnrollee.getInsuranceCompany()).set(i, newEnrollee);
                            isNew = false;
                            break;
                        } else {
                            isNew = false;
                        }
                    }
                }
                //Add New Enrollee object
                if (isNew) {
                    hashMap.get(newEnrollee.getInsuranceCompany()).add(newEnrollee);
                }
            } else {
                //Create new Enrollee list and map to insurance company
                List<Enrollee> newList = new ArrayList<Enrollee>();
                newList.add(newEnrollee);
                hashMap.put(newEnrollee.getInsuranceCompany(), newList);
            }
        }
        return hashMap;
    }

    public static Enrollee getNewEmployee(CSVRecord csvRecord) {
        String userId = csvRecord.get("userId");
        String firstName = csvRecord.get("firstName");
        String lastName = csvRecord.get("lastName");
        Integer version = Integer.parseInt(csvRecord.get("version"));
        String insuranceCompany = csvRecord.get("insuranceCompany");

        //Set Values into Enrollee Object
        Enrollee newEnrollee = new Enrollee();
        newEnrollee.setUserId(userId);
        newEnrollee.setFirstName(firstName);
        newEnrollee.setLastName(lastName);
        newEnrollee.setVersion(version);
        newEnrollee.setInsuranceCompany(insuranceCompany);
        return newEnrollee;
    }

    public static void createNewCSVFiles(HashMap<String, List<Enrollee>> hashMap) throws IOException {
        //File Number for File Name
        int fileNum = 1;
        //Iterate through Map for List of Enrollees
        for (Map.Entry<String, List<Enrollee>> entry : hashMap.entrySet()) {
            //Sort Enrollee List by Names
            Collections.sort(hashMap.get(entry.getValue().get(0).getInsuranceCompany()), compareByName);

            String fileName = "/Users/admin/Desktop/AvailityHW3/" + fileNum + ".csv";
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fw);
            //Printer for new CSV files
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("userId", "firstName", "lastName", "version", "insuranceCompany"));
            for (int i = 0; i < hashMap.get(entry.getValue().get(0).getInsuranceCompany()).size(); i++) {
                //Create new record for csv file
                csvPrinter.printRecord(entry.getValue().get(i).getUserId(), entry.getValue().get(i).getFirstName(),
                        entry.getValue().get(i).getLastName(), entry.getValue().get(i).getVersion(),
                        entry.getValue().get(i).getInsuranceCompany());
            }
            //Increment File Number for name
            fileNum++;
            csvPrinter.flush();
        }

    }

    //Comparator to sort Enrollees by Name
    static Comparator<Enrollee> compareByName = new Comparator<Enrollee>() {
        //        @Override
        public int compare(Enrollee o1, Enrollee o2) {
            if (o1.getLastName().equals(o2.getLastName())) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
            return o1.getLastName().compareTo(o2.getLastName());
        }
    };
}
