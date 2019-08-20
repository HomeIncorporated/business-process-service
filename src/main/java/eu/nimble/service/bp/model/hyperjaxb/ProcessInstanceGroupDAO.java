//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.05.29 at 02:01:18 PM MSK
//


package eu.nimble.service.bp.model.hyperjaxb;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.hyperjaxb3.item.Item;
import org.jvnet.hyperjaxb3.item.ItemUtils;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


/**
 * <p>Java class for ProcessInstanceGroupDAO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessInstanceGroupDAO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="partyID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="processInstanceIDs" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *         &lt;element name="archived" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="collaborationRole" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="status" type="{}GroupStatus"/&gt;
 *         &lt;element name="associatedGroups" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *         &lt;element name="precedingProcessInstanceGroup" type="{}ProcessInstanceGroupDAO" minOccurs="0"/&gt;
 *         &lt;element name="firstActivityTime" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="lastActivityTime" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessInstanceGroupDAO", propOrder = {
    "id",
    "name",
    "partyID",
    "processInstanceIDs",
    "archived",
    "collaborationRole",
    "status",
    "associatedGroups",
    "precedingProcessInstanceGroup",
    "firstActivityTime",
    "lastActivityTime"
})
@Entity(name = "ProcessInstanceGroupDAO")
@Table(name = "PROCESS_INSTANCE_GROUP_DAO")
@Inheritance(strategy = InheritanceType.JOINED)
public class ProcessInstanceGroupDAO
    implements Equals
{

    @XmlElement(name = "ID", required = true)
    protected String id;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(required = true)
    protected String partyID;
    @XmlElement(required = true)
    protected List<String> processInstanceIDs;
    protected boolean archived;
    @XmlElement(required = true)
    protected String collaborationRole;
    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected GroupStatus status;
    @XmlElement(required = true)
    protected List<String> associatedGroups;
    protected ProcessInstanceGroupDAO precedingProcessInstanceGroup;
    @XmlElement(required = true)
    protected String firstActivityTime;
    @XmlElement(required = true)
    protected String lastActivityTime;
    @XmlAttribute(name = "Hjid")
    protected Long hjid;
    protected transient List<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem> processInstanceIDsItems;
    protected transient List<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem> associatedGroupsItems;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "ID", length = 255)
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "NAME_", length = 255)
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the partyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "PARTY_ID", length = 255)
    public String getPartyID() {
        return partyID;
    }

    /**
     * Sets the value of the partyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyID(String value) {
        this.partyID = value;
    }

    /**
     * Gets the value of the processInstanceIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processInstanceIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessInstanceIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Transient
    public List<String> getProcessInstanceIDs() {
        if (processInstanceIDs == null) {
            processInstanceIDs = new ArrayList<String>();
        }
        return this.processInstanceIDs;
    }

    /**
     * 
     * 
     */
    public void setProcessInstanceIDs(List<String> processInstanceIDs) {
        this.processInstanceIDs = processInstanceIDs;
    }

    /**
     * Gets the value of the archived property.
     * 
     */
    @Basic
    @Column(name = "ARCHIVED")
    public boolean isArchived() {
        return archived;
    }

    /**
     * Sets the value of the archived property.
     * 
     */
    public void setArchived(boolean value) {
        this.archived = value;
    }

    /**
     * Gets the value of the collaborationRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "COLLABORATION_ROLE", length = 255)
    public String getCollaborationRole() {
        return collaborationRole;
    }

    /**
     * Sets the value of the collaborationRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollaborationRole(String value) {
        this.collaborationRole = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link GroupStatus }
     *     
     */
    @Basic
    @Column(name = "STATUS", length = 255)
    @Enumerated(EnumType.STRING)
    public GroupStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupStatus }
     *     
     */
    public void setStatus(GroupStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the associatedGroups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the associatedGroups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssociatedGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Transient
    public List<String> getAssociatedGroups() {
        if (associatedGroups == null) {
            associatedGroups = new ArrayList<String>();
        }
        return this.associatedGroups;
    }

    /**
     * 
     * 
     */
    public void setAssociatedGroups(List<String> associatedGroups) {
        this.associatedGroups = associatedGroups;
    }

    /**
     * Gets the value of the precedingProcessInstanceGroup property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessInstanceGroupDAO }
     *     
     */
    @OneToOne(targetEntity = ProcessInstanceGroupDAO.class, cascade = {
        CascadeType.REFRESH,
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    public ProcessInstanceGroupDAO getPrecedingProcessInstanceGroup() {
        return precedingProcessInstanceGroup;
    }

    /**
     * Sets the value of the precedingProcessInstanceGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessInstanceGroupDAO }
     *     
     */
    public void setPrecedingProcessInstanceGroup(ProcessInstanceGroupDAO value) {
        this.precedingProcessInstanceGroup = value;
    }

    /**
     * Gets the value of the firstActivityTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Transient
    public String getFirstActivityTime() {
        return firstActivityTime;
    }

    /**
     * Sets the value of the firstActivityTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstActivityTime(String value) {
        this.firstActivityTime = value;
    }

    /**
     * Gets the value of the lastActivityTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Transient
    public String getLastActivityTime() {
        return lastActivityTime;
    }

    /**
     * Sets the value of the lastActivityTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastActivityTime(String value) {
        this.lastActivityTime = value;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final ProcessInstanceGroupDAO that = ((ProcessInstanceGroupDAO) object);
        {
            String lhsID;
            lhsID = this.getID();
            String rhsID;
            rhsID = that.getID();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "id", lhsID), LocatorUtils.property(thatLocator, "id", rhsID), lhsID, rhsID)) {
                return false;
            }
        }
        {
            String lhsName;
            lhsName = this.getName();
            String rhsName;
            rhsName = that.getName();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "name", lhsName), LocatorUtils.property(thatLocator, "name", rhsName), lhsName, rhsName)) {
                return false;
            }
        }
        {
            String lhsPartyID;
            lhsPartyID = this.getPartyID();
            String rhsPartyID;
            rhsPartyID = that.getPartyID();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "partyID", lhsPartyID), LocatorUtils.property(thatLocator, "partyID", rhsPartyID), lhsPartyID, rhsPartyID)) {
                return false;
            }
        }
        {
            List<String> lhsProcessInstanceIDs;
            lhsProcessInstanceIDs = (((this.processInstanceIDs!= null)&&(!this.processInstanceIDs.isEmpty()))?this.getProcessInstanceIDs():null);
            List<String> rhsProcessInstanceIDs;
            rhsProcessInstanceIDs = (((that.processInstanceIDs!= null)&&(!that.processInstanceIDs.isEmpty()))?that.getProcessInstanceIDs():null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "processInstanceIDs", lhsProcessInstanceIDs), LocatorUtils.property(thatLocator, "processInstanceIDs", rhsProcessInstanceIDs), lhsProcessInstanceIDs, rhsProcessInstanceIDs)) {
                return false;
            }
        }
        {
            boolean lhsArchived;
            lhsArchived = this.isArchived();
            boolean rhsArchived;
            rhsArchived = that.isArchived();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "archived", lhsArchived), LocatorUtils.property(thatLocator, "archived", rhsArchived), lhsArchived, rhsArchived)) {
                return false;
            }
        }
        {
            String lhsCollaborationRole;
            lhsCollaborationRole = this.getCollaborationRole();
            String rhsCollaborationRole;
            rhsCollaborationRole = that.getCollaborationRole();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "collaborationRole", lhsCollaborationRole), LocatorUtils.property(thatLocator, "collaborationRole", rhsCollaborationRole), lhsCollaborationRole, rhsCollaborationRole)) {
                return false;
            }
        }
        {
            GroupStatus lhsStatus;
            lhsStatus = this.getStatus();
            GroupStatus rhsStatus;
            rhsStatus = that.getStatus();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "status", lhsStatus), LocatorUtils.property(thatLocator, "status", rhsStatus), lhsStatus, rhsStatus)) {
                return false;
            }
        }
        {
            List<String> lhsAssociatedGroups;
            lhsAssociatedGroups = (((this.associatedGroups!= null)&&(!this.associatedGroups.isEmpty()))?this.getAssociatedGroups():null);
            List<String> rhsAssociatedGroups;
            rhsAssociatedGroups = (((that.associatedGroups!= null)&&(!that.associatedGroups.isEmpty()))?that.getAssociatedGroups():null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "associatedGroups", lhsAssociatedGroups), LocatorUtils.property(thatLocator, "associatedGroups", rhsAssociatedGroups), lhsAssociatedGroups, rhsAssociatedGroups)) {
                return false;
            }
        }
        {
            ProcessInstanceGroupDAO lhsPrecedingProcessInstanceGroup;
            lhsPrecedingProcessInstanceGroup = this.getPrecedingProcessInstanceGroup();
            ProcessInstanceGroupDAO rhsPrecedingProcessInstanceGroup;
            rhsPrecedingProcessInstanceGroup = that.getPrecedingProcessInstanceGroup();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "precedingProcessInstanceGroup", lhsPrecedingProcessInstanceGroup), LocatorUtils.property(thatLocator, "precedingProcessInstanceGroup", rhsPrecedingProcessInstanceGroup), lhsPrecedingProcessInstanceGroup, rhsPrecedingProcessInstanceGroup)) {
                return false;
            }
        }
        {
            String lhsFirstActivityTime;
            lhsFirstActivityTime = this.getFirstActivityTime();
            String rhsFirstActivityTime;
            rhsFirstActivityTime = that.getFirstActivityTime();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "firstActivityTime", lhsFirstActivityTime), LocatorUtils.property(thatLocator, "firstActivityTime", rhsFirstActivityTime), lhsFirstActivityTime, rhsFirstActivityTime)) {
                return false;
            }
        }
        {
            String lhsLastActivityTime;
            lhsLastActivityTime = this.getLastActivityTime();
            String rhsLastActivityTime;
            rhsLastActivityTime = that.getLastActivityTime();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "lastActivityTime", lhsLastActivityTime), LocatorUtils.property(thatLocator, "lastActivityTime", rhsLastActivityTime), lhsLastActivityTime, rhsLastActivityTime)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    /**
     * Gets the value of the hjid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    @Id
    @Column(name = "HJID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getHjid() {
        return hjid;
    }

    /**
     * Sets the value of the hjid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHjid(Long value) {
        this.hjid = value;
    }

    @OneToMany(targetEntity = ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "PROCESS_INSTANCE_IDS_ITEMS_P_0")
    public List<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem> getProcessInstanceIDsItems() {
        if (this.processInstanceIDsItems == null) {
            this.processInstanceIDsItems = new ArrayList<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem>();
        }
        if (ItemUtils.shouldBeWrapped(this.processInstanceIDs)) {
            this.processInstanceIDs = ItemUtils.wrap(this.processInstanceIDs, this.processInstanceIDsItems, ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem.class);
        }
        return this.processInstanceIDsItems;
    }

    public void setProcessInstanceIDsItems(List<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem> value) {
        this.processInstanceIDs = null;
        this.processInstanceIDsItems = null;
        this.processInstanceIDsItems = value;
        if (this.processInstanceIDsItems == null) {
            this.processInstanceIDsItems = new ArrayList<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem>();
        }
        if (ItemUtils.shouldBeWrapped(this.processInstanceIDs)) {
            this.processInstanceIDs = ItemUtils.wrap(this.processInstanceIDs, this.processInstanceIDsItems, ProcessInstanceGroupDAO.ProcessInstanceGroupDAOProcessInstanceIDsItem.class);
        }
    }

    @OneToMany(targetEntity = ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "ASSOCIATED_GROUPS_ITEMS_PROC_0")
    public List<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem> getAssociatedGroupsItems() {
        if (this.associatedGroupsItems == null) {
            this.associatedGroupsItems = new ArrayList<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem>();
        }
        if (ItemUtils.shouldBeWrapped(this.associatedGroups)) {
            this.associatedGroups = ItemUtils.wrap(this.associatedGroups, this.associatedGroupsItems, ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem.class);
        }
        return this.associatedGroupsItems;
    }

    public void setAssociatedGroupsItems(List<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem> value) {
        this.associatedGroups = null;
        this.associatedGroupsItems = null;
        this.associatedGroupsItems = value;
        if (this.associatedGroupsItems == null) {
            this.associatedGroupsItems = new ArrayList<ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem>();
        }
        if (ItemUtils.shouldBeWrapped(this.associatedGroups)) {
            this.associatedGroups = ItemUtils.wrap(this.associatedGroups, this.associatedGroupsItems, ProcessInstanceGroupDAO.ProcessInstanceGroupDAOAssociatedGroupsItem.class);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Entity(name = "ProcessInstanceGroupDAO$ProcessInstanceGroupDAOAssociatedGroupsItem")
    @Table(name = "PROCESS_INSTANCE_GROUP_DAOAS_0")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class ProcessInstanceGroupDAOAssociatedGroupsItem
        implements Item<String>
    {

        @XmlElement(name = "associatedGroups")
        protected String item;
        @XmlAttribute(name = "Hjid")
        protected Long hjid;

        /**
         * Gets the value of the item property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        @Basic
        @Column(name = "ITEM", length = 255)
        public String getItem() {
            return item;
        }

        /**
         * Sets the value of the item property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setItem(String value) {
            this.item = value;
        }

        /**
         * Gets the value of the hjid property.
         * 
         * @return
         *     possible object is
         *     {@link Long }
         *     
         */
        @Id
        @Column(name = "HJID")
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long getHjid() {
            return hjid;
        }

        /**
         * Sets the value of the hjid property.
         * 
         * @param value
         *     allowed object is
         *     {@link Long }
         *     
         */
        public void setHjid(Long value) {
            this.hjid = value;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Entity(name = "ProcessInstanceGroupDAO$ProcessInstanceGroupDAOProcessInstanceIDsItem")
    @Table(name = "PROCESS_INSTANCE_GROUP_DAOPR_0")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class ProcessInstanceGroupDAOProcessInstanceIDsItem
        implements Item<String>
    {

        @XmlElement(name = "processInstanceIDs")
        protected String item;
        @XmlAttribute(name = "Hjid")
        protected Long hjid;

        /**
         * Gets the value of the item property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        @Basic
        @Column(name = "ITEM", length = 255)
        public String getItem() {
            return item;
        }

        /**
         * Sets the value of the item property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setItem(String value) {
            this.item = value;
        }

        /**
         * Gets the value of the hjid property.
         * 
         * @return
         *     possible object is
         *     {@link Long }
         *     
         */
        @Id
        @Column(name = "HJID")
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long getHjid() {
            return hjid;
        }

        /**
         * Sets the value of the hjid property.
         * 
         * @param value
         *     allowed object is
         *     {@link Long }
         *     
         */
        public void setHjid(Long value) {
            this.hjid = value;
        }

    }

}
