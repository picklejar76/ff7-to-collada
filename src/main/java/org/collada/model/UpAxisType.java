//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.01 at 05:40:54 PM CST 
//


package org.collada.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpAxisType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UpAxisType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="X_UP"/>
 *     &lt;enumeration value="Y_UP"/>
 *     &lt;enumeration value="Z_UP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UpAxisType")
@XmlEnum
public enum UpAxisType {

    X_UP,
    Y_UP,
    Z_UP;

    public String value() {
        return name();
    }

    public static UpAxisType fromValue(String v) {
        return valueOf(v);
    }

}
