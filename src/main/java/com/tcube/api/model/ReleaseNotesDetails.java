package com.tcube.api.model;


import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Release_notes")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})

public class ReleaseNotesDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "org_id", referencedColumnName = "org_id")
    private OrgDetails orgDetails;
    
    @Column(name="created_time")
    private Date created_time;
    
    @Column(name="modified_time")
    private Date modified_time;
    
    @Column(name="logoFileName")
    private String logoFileName;
    
    @Column(name ="releaseNotesTitle")
    private String releaseNotesTitle;
    
    @Column(name="keyNote")
    private String keyNote;
  
	@Column(name="productName")
    private String productName;
    
 
	@Column(name="version")
    private String version;
    
    @Column(name="dor")
    private Date dor;
        
    @Column(name="company_logo")
    @Lob
    private String companyLogo;
    
    
    @Column(name="whatsNew")
    private String whatsNew;
    
    @Column(name="improvement")
    private String improvement;
    
    @Column(name="bugFixes")
    private String bugFixes;
    
    @Column(name="general")
    private String general;
    
    @Column(name="comingsoon")
    private String comingsoon;
    
    @Lob
    @Column(name="notes_pdfFormat")
    private byte[] notes_pdfFormat;
        

    private Boolean is_deleted = Boolean.FALSE;
    
    private Boolean is_publish = Boolean.FALSE;
    
    public Boolean getIs_publish() {
		return is_publish;
	}

	public void setIs_publish(Boolean is_publish) {
		this.is_publish = is_publish;
	}

	public Boolean getIs_republish() {
		return is_republish;
	}

	public void setIs_republish(Boolean is_republish) {
		this.is_republish = is_republish;
	}

	private Boolean is_republish = Boolean.FALSE;
    
    public String getReleaseNotesTitle() {
  		return releaseNotesTitle;
  	}

  	public void setReleaseNotesTitle(String releaseNotesTitle) {
  		this.releaseNotesTitle = releaseNotesTitle;
  	}

    public String getBugFixes() {
		return bugFixes;
	}

	public void setBugFixes(String bugFixes) {
		this.bugFixes = bugFixes;
	}

	public String getComingsoon() {
		return comingsoon;
	}

	public void setComingsoon(String comingsoon) {
		this.comingsoon = comingsoon;
	}

	
    public String getKeyNote() {
 		return keyNote;
 	}

 	public void setKeyNote(String keyNote) {
 		this.keyNote = keyNote;
 	}

	public String getGeneral() {
		return general;
	}

	public void setGeneral(String general) {
		this.general = general;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getWhatsNew() {
		return whatsNew;
	}

	public void setWhatsNew(String whatsNew) {
		this.whatsNew = whatsNew;
	}

	public String getImprovement() {
		return improvement;
	}

	public void setImprovement(String improvement) {
		this.improvement = improvement;
	}

	public String getEndUserImpact() {
		return bugFixes;
	}

	public void setEndUserImpact(String bugFixes) {
		this.bugFixes = bugFixes;
	}

	@Column(name="companyLink")
    private String companyLink;
    
    public byte[] getNotes_pdfFormat() {
        return notes_pdfFormat;
    }

    public void setNotes_pdfFormat(byte[] notes_pdfFormat) {
        this.notes_pdfFormat = notes_pdfFormat;
    }

   
    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
    
    public String getLogoFileName() {
        return logoFileName;
    }

    public void setLogoFileName(String logoFileName) {
        this.logoFileName = logoFileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrgDetails getOrgDetails() {
        return orgDetails;
    }

    public void setOrgDetails(OrgDetails orgDetails) {
        this.orgDetails = orgDetails;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDor() {
        return dor;
    }

    public void setDor(Date dor) {
        this.dor = dor;
    }

	public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Date getModified_time() {
        return modified_time;
    }

    public void setModified_time(Date modified_time) {
        this.modified_time = modified_time;
    }


    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyLink() {
        return companyLink;
    }

    public void setCompanyLink(String companyLink) {
        this.companyLink = companyLink;
    }
   
    
}
