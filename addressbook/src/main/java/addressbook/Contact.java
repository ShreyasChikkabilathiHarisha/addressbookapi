package addressbook;

import java.util.UUID;

public class Contact {
//	private String id;
	private String name;
	private String number;

	public Contact(String name, String number) {
//		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	// Overriding equals() to compare two Complex objects 
    @Override
    public boolean equals(Object o) { 
  
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Contact)) { 
            return false; 
        } 
          
        // typecast o to Complex so that we can compare data members  
        Contact c = (Contact) o; 
          
        // Compare the data members and return accordingly  
        return (name.equals(c.name) && number.equals(c.number)); 
    } 
}
