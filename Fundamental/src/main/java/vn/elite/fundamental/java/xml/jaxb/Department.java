package vn.elite.fundamental.java.xml.jaxb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(namespace = "//vn.elite/jaxb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Department {
    private String deptNo;
    private String deptName;
    private String location;

    @XmlElementWrapper(name = "employees")
    @XmlElement(name = "employee")
    private List<Employee> employees;
}
