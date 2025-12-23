package app.controllers;

public class BloodCompatibilityController {
    
    public boolean canTransfuse(String donorBloodType, String recipientBloodType) {
        // O- can donate to anyone
        if (donorBloodType.equals("O-")) return true;
        
        // Same blood type
        if (donorBloodType.equals(recipientBloodType)) return true;
        
        // Rh compatibility
        String donorRh = donorBloodType.substring(donorBloodType.length() - 1);
        String recipientRh = recipientBloodType.substring(recipientBloodType.length() - 1);
        
        if (donorRh.equals("-") && recipientRh.equals("+")) return false;
        
        // AB+ can receive from anyone
        if (recipientBloodType.equals("AB+")) return true;
        
        // Match base type
        String donorBase = donorBloodType.substring(0, donorBloodType.length() - 1);
        String recipientBase = recipientBloodType.substring(0, recipientBloodType.length() - 1);
        
        return donorBase.equals(recipientBase) || donorBase.equals("O");
    }
    
    public String[] getCompatibleDonors(String bloodType) {
        switch (bloodType) {
            case "O+": return new String[]{"O+", "O-"};
            case "O-": return new String[]{"O-"};
            case "A+": return new String[]{"A+", "A-", "O+", "O-"};
            case "A-": return new String[]{"A-", "O-"};
            case "B+": return new String[]{"B+", "B-", "O+", "O-"};
            case "B-": return new String[]{"B-", "O-"};
            case "AB+": return new String[]{"AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-"};
            case "AB-": return new String[]{"AB-", "A-", "B-", "O-"};
            default: return new String[]{};
        }
    }
}