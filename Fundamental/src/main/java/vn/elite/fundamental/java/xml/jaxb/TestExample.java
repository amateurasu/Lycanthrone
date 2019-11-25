package vn.elite.fundamental.java.xml.jaxb;

import lombok.val;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class TestExample {
    private static final String XML_FILE = "dept-info.xml";

    public static void main(String[] args) {

        val emp1 = new Employee("E01", "Tom", null);
        val emp2 = new Employee("E02", "Mary", "E01");
        val emp3 = new Employee("E03", "John", null);

        val list = new ArrayList<Employee>() {{
            add(emp1);
            add(emp2);
            add(emp3);
        }};

        val department = new Department("D01", "ACCOUNTING", "NEW YORK", list);

        try {
            val context = JAXBContext.newInstance(Department.class);
            val m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(department, System.out);
            m.marshal(department, new File(XML_FILE));

            val um = context.createUnmarshaller();
            val deptFromFile = (Department) um.unmarshal(new FileReader(XML_FILE));
            val employeeList = deptFromFile.getEmployees();
            for (Employee employee : employeeList) {
                System.out.format("Employee: %s\n", employee.getEmpName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}