package chandraprasetyo.restful.service;

import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.ContactResponse;
import chandraprasetyo.restful.model.CreateContactRequest;
import chandraprasetyo.restful.model.SearchContactRequest;
import chandraprasetyo.restful.model.UpdateContactRequest;
import chandraprasetyo.restful.repository.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

public interface ContactService {

    public ContactResponse create(User user, CreateContactRequest request);

    public ContactResponse get(User user, String id);

    public ContactResponse update(User user, UpdateContactRequest request);

    public void delete(User user, String contactId);

    public Page<ContactResponse> search(User user, SearchContactRequest request);

}
