package chandraprasetyo.restful.service;

import chandraprasetyo.restful.entity.Contact;
import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.AddressResponse;
import chandraprasetyo.restful.model.CreateAddressRequest;
import chandraprasetyo.restful.model.UpdateAddressRequest;

import java.util.List;

public interface AddressService {

    public AddressResponse create(User user, CreateAddressRequest request);

    public AddressResponse get(User user, String contactId, String addressId);

    public AddressResponse update(User user, UpdateAddressRequest request);

    public void delete(User user, String contactId, String addressId);

    public List<AddressResponse> list(User user, String contactId);

}
