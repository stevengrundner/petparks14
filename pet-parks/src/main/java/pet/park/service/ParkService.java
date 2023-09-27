package pet.park.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.park.controller.model.ContributorData;
import pet.park.controller.model.PetParkData;
import pet.park.dao.AmenityDao;
import pet.park.dao.ContributorDao;
import pet.park.dao.PetParkDao;
import pet.park.entity.Amenity;
import pet.park.entity.Contributor;
import pet.park.entity.PetPark;

@Service
public class ParkService {

	@Autowired
	private ContributorDao contributorDao;

	@Autowired
	private AmenityDao amenityDao;

	@Autowired
	private PetParkDao petParkDao;

	@Transactional(readOnly = false)
	public ContributorData saveContributor(ContributorData contributorData) {
		Long contributorId = contributorData.getContributorId();
		Contributor contributor = findOrCreateContributor(contributorId, contributorData.getContributorEmail());

		setFieldsInContributor(contributor, contributorData);
		return new ContributorData(contributorDao.save(contributor));
	} // (14-3 Global Error Handler) -> checking to make sure there isn't duplicate
		// email

	private void setFieldsInContributor(Contributor contributor, ContributorData contributorData) {
		contributor.setContributorEmail(contributorData.getContributorEmail());
		contributor.setContributorName(contributorData.getContributorName());
	}

	private Contributor findOrCreateContributor(Long contributorId, String contributorEmail) {
		Contributor contributor;

		if (Objects.isNull(contributorId)) {
			Optional<Contributor> opContrib = contributorDao.findByContributorEmail(contributorEmail);

			if (opContrib.isPresent()) {
				throw new DuplicateKeyException("Contributor with email " + contributorEmail + " already exists.");
			}

			contributor = new Contributor();

		} else {
			contributor = findContributorById(contributorId);
		}

		return contributor;
	} // (14-3 Global Error Handler) -> checking to make sure there isn't duplicate
		// email

	private Contributor findContributorById(Long contributorId) {
		return contributorDao.findById(contributorId).orElseThrow(
				() -> new NoSuchElementException("Contributor with ID=" + contributorId + " was not found."));
	}

	@Transactional(readOnly = true)
	public List<ContributorData> retrieveAllContributors() {
		List<Contributor> contributors = contributorDao.findAll();
		java.util.List<ContributorData> response = new LinkedList<>();

		for (Contributor contributor : contributors) {
			response.add(new ContributorData(contributor));
		}

		return response;
	} // (VIDEO 14-2 Retrieve Contributor) - retrieve all contributors in list

	@Transactional(readOnly = true)
	public ContributorData retrieveContributorById(Long contributorId) {
		Contributor contributor = findContributorById(contributorId);
		return new ContributorData(contributor);
	} // (VIDEO 14-2 Retrieve Contributor) - retrieve one contributor at a time by ID

	@Transactional(readOnly = false)
	public void deleteContributorById(Long contributorId) {
		Contributor contributor = findContributorById(contributorId);
		contributorDao.delete(contributor);
	} // (VIDEO 14-5 Delete Contributor) - delete contributor method

	@Transactional(readOnly = false)
	public PetParkData savePetPark(Long contributorId, PetParkData petParkData) {
		Contributor contributor = findContributorById(contributorId);

		Set<Amenity> amenities = amenityDao.findAllByAmenityIn(petParkData.getAmenities());

		PetPark petPark = findOrCreatePetPark(petParkData.getPetParkId());
		setPetParkFields(petPark, petParkData);

		petPark.setContributor(contributor);
		contributor.getPetParks().add(petPark);

		for (Amenity amenity : amenities) {
			amenity.getPetParks().add(petPark);
			petPark.getAmenities().add(amenity);
		}

		PetPark dbPetPark = petParkDao.save(petPark);
		return new PetParkData(dbPetPark);

	} // (14-6 Create Location)

	private void setPetParkFields(PetPark petPark, PetParkData petParkData) {
		petPark.setCountry(petParkData.getCountry());
		petPark.setDirections(petParkData.getDirections());
		petPark.setGeoLocation(petParkData.getGeoLocation());
		petPark.setParkName(petParkData.getParkName());
		petPark.setPetParkId(petParkData.getPetParkId());
		petPark.setStateOrProvince(petParkData.getStateOrProvince());

	}

	private PetPark findOrCreatePetPark(Long petParkId) {
		PetPark petPark;

		if (Objects.isNull(petParkId)) {
			petPark = new PetPark();
		} else {
			petPark = findPetParkById(petParkId);

		}

		return petPark;
	} // (14-6 Create Location)

	private PetPark findPetParkById(Long petParkId) {
		return petParkDao.findById(petParkId)
				.orElseThrow(() -> new NoSuchElementException("Pet park with ID=" + petParkId + " does not exist."));
	} // (14-6 Create Location)

	@Transactional(readOnly = true)
	public PetParkData retrievePetParkById(Long contributorId, Long parkId) {
		findContributorById(contributorId);
		PetPark petPark = findPetParkById(parkId);

		if (petPark.getContributor().getContributorId() != contributorId) {
			throw new IllegalStateException(
					"Pet park with ID=" + parkId + " is not owned by contributor with ID=" + contributorId);
		}

		return new PetParkData(petPark);
	}

}
