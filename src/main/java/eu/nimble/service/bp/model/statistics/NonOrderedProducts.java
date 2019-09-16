package eu.nimble.service.bp.model.statistics;

import eu.nimble.service.model.ubl.commonaggregatecomponents.ItemIdentificationType;
import eu.nimble.service.model.ubl.commonaggregatecomponents.ItemType;
import eu.nimble.service.model.ubl.commonbasiccomponents.TextType;

import java.util.*;

/**
 * Created by suat on 22-Jun-18.
 */
public class NonOrderedProducts {
    private Map<String, PartyItems> companies = new HashMap<>();

    public Map getCompanies() {
        return companies;
    }

    public void addProduct(String partyId, String partyName, String itemManId, String itemName) {
        // check company map
        PartyItems companyProducts = companies.get(partyId);
        if(companyProducts == null) {
            companyProducts = new PartyItems();
            companyProducts.setPartyName(partyName);
            companies.put(partyId, companyProducts);
        }

        ItemType item = new ItemType();
        item.setManufacturersItemIdentification(new ItemIdentificationType());
        item.getManufacturersItemIdentification().setID(itemManId);
        TextType textType = new TextType();
        textType.setValue(itemName);
        textType.setLanguageID("en");
        item.setName(Arrays.asList(textType));
        companyProducts.getProducts().add(item);
    }

    private static class PartyItems {
        private String partyName;
        private List<ItemType> products = new ArrayList<>();

        public String getPartyName() {
            return partyName;
        }

        public void setPartyName(String partyName) {
            this.partyName = partyName;
        }

        public List<ItemType> getProducts() {
            return products;
        }

        public void setProducts(List<ItemType> products) {
            this.products = products;
        }
    }
}
