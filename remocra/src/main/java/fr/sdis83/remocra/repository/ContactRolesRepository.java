package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT_ROLES;

@Configuration
public class ContactRolesRepository {
    @Autowired
    DSLContext context;

    ContactRolesRepository(DSLContext context){this.context = context;}

    public ContactRolesRepository(){}

    @Bean
    public ContactRolesRepository contactRolesRepository(DSLContext context){
        return new ContactRolesRepository(context);
    }

    public void deleteContactRolesById(Long idContact){
        context.deleteFrom(CONTACT_ROLES)
                .where(CONTACT_ROLES.CONTACT.eq(idContact))
                .execute();
    }


    public void createContactRoles(Long idContact, Long idRole){
        context.insertInto(CONTACT_ROLES,
                CONTACT_ROLES.CONTACT, CONTACT_ROLES.ROLES)
                .values(idContact, idRole)
                .execute();
    }
}
