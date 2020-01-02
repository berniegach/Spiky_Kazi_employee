package com.spikingacacia.spikykaziemployee.database;

import java.util.LinkedHashMap;

/**
 * Created by $USER_NAME on 10/3/2018.
 **/
public class UGlobalInfo
{
    private int staffCount;
    private int compliant;
    private int noncompliant;
    private LinkedHashMap<String,Character>complaintStaff;
    private LinkedHashMap<String,Character>nonComplaintStaff;
    private int missingQualifications;
    private int missingCertificates;
    private int expiredCertificates;
    private int usersWithMissingQualifications;
    private int usersWithMissingCertificates;
    private int userWithExpiredCertificates;
    private int usersWithCase;
    private int usersWithQCCase;
    private int usersWithQECCase;
    private int usersWithCECCase;
    private int usersWithQCECCase;
    private int []eachQualMissingCount;
    private String []tableColumns;

    public UGlobalInfo()
    {
    }

    public UGlobalInfo(int staffCount, int compliant, int noncompliant, LinkedHashMap<String, Character> complaintStaff, LinkedHashMap<String, Character> nonComplaintStaff, int missingQualifications, int missingCertificates, int expiredCertificates, int usersWithMissingQualifications, int usersWithMissingCertificates, int userWithExpiredCertificates, int usersWithCase, int usersWithQCCase, int usersWithQECCase, int usersWithCECCase, int usersWithQCECCase, int[] eachQualMissingCount, String[] tableColumns)
    {
        this.staffCount = staffCount;
        this.compliant = compliant;
        this.noncompliant = noncompliant;
        this.complaintStaff=complaintStaff;
        this.nonComplaintStaff=nonComplaintStaff;
        this.missingQualifications = missingQualifications;
        this.missingCertificates = missingCertificates;
        this.expiredCertificates = expiredCertificates;
        this.usersWithMissingQualifications = usersWithMissingQualifications;
        this.usersWithMissingCertificates = usersWithMissingCertificates;
        this.userWithExpiredCertificates = userWithExpiredCertificates;
        this.usersWithCase = usersWithCase;
        this.usersWithQCCase = usersWithQCCase;
        this.usersWithQECCase = usersWithQECCase;
        this.usersWithCECCase = usersWithCECCase;
        this.usersWithQCECCase = usersWithQCECCase;
        this.eachQualMissingCount = eachQualMissingCount;
        this.tableColumns = tableColumns;
    }

    public int getStaffCount()
    {
        return staffCount;
    }

    public void setStaffCount(int staffCount)
    {
        this.staffCount = staffCount;
    }

    public int getCompliant()
    {
        return compliant;
    }

    public void setCompliant(int compliant)
    {
        this.compliant = compliant;
    }

    public int getNoncompliant()
    {
        return noncompliant;
    }

    public void setNoncompliant(int noncompliant)
    {
        this.noncompliant = noncompliant;
    }

    public LinkedHashMap<String, Character> getComplaintStaff()
    {
        return complaintStaff;
    }

    public void setComplaintStaff(LinkedHashMap<String, Character> complaintStaff)
    {
        this.complaintStaff = complaintStaff;
    }

    public LinkedHashMap<String, Character> getNonComplaintStaff()
    {
        return nonComplaintStaff;
    }

    public void setNonComplaintStaff(LinkedHashMap<String, Character> nonComplaintStaff)
    {
        this.nonComplaintStaff = nonComplaintStaff;
    }
    public int getMissingQualifications()
    {
        return missingQualifications;
    }

    public void setMissingQualifications(int missingQualifications)
    {
        this.missingQualifications = missingQualifications;
    }

    public int getMissingCertificates()
    {
        return missingCertificates;
    }

    public void setMissingCertificates(int missingCertificates)
    {
        this.missingCertificates = missingCertificates;
    }

    public int getExpiredCertificates()
    {
        return expiredCertificates;
    }

    public void setExpiredCertificates(int expiredCertificates)
    {
        this.expiredCertificates = expiredCertificates;
    }

    public int getUsersWithMissingQualifications()
    {
        return usersWithMissingQualifications;
    }

    public void setUsersWithMissingQualifications(int usersWithMissingQualifications)
    {
        this.usersWithMissingQualifications = usersWithMissingQualifications;
    }

    public int getUsersWithMissingCertificates()
    {
        return usersWithMissingCertificates;
    }

    public void setUsersWithMissingCertificates(int usersWithMissingCertificates)
    {
        this.usersWithMissingCertificates = usersWithMissingCertificates;
    }

    public int getUserWithExpiredCertificates()
    {
        return userWithExpiredCertificates;
    }

    public void setUserWithExpiredCertificates(int userWithExpiredCertificates)
    {
        this.userWithExpiredCertificates = userWithExpiredCertificates;
    }

    public int getUsersWithCase()
    {
        return usersWithCase;
    }

    public void setUsersWithCase(int usersWithCase)
    {
        this.usersWithCase = usersWithCase;
    }

    public int getUsersWithQCCase()
    {
        return usersWithQCCase;
    }

    public void setUsersWithQCCase(int usersWithQCCase)
    {
        this.usersWithQCCase = usersWithQCCase;
    }

    public int getUsersWithQECCase()
    {
        return usersWithQECCase;
    }

    public void setUsersWithQECCase(int usersWithQECCase)
    {
        this.usersWithQECCase = usersWithQECCase;
    }

    public int getUsersWithCECCase()
    {
        return usersWithCECCase;
    }

    public void setUsersWithCECCase(int usersWithCECCase)
    {
        this.usersWithCECCase = usersWithCECCase;
    }

    public int getUsersWithQCECCase()
    {
        return usersWithQCECCase;
    }

    public void setUsersWithQCECCase(int usersWithQCECCase)
    {
        this.usersWithQCECCase = usersWithQCECCase;
    }

    public int[] getEachQualMissingCount()
    {
        return eachQualMissingCount;
    }

    public void setEachQualMissingCount(int[] eachQualMissingCount)
    {
        this.eachQualMissingCount = eachQualMissingCount;
    }

    public String[] getTableColumns()
    {
        return tableColumns;
    }

    public void setTableColumns(String[] tableColumns)
    {
        this.tableColumns = tableColumns;
    }
}
