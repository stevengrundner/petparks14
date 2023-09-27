package pet.park.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.park.controller.model.ContributorData;
import pet.park.controller.model.PetParkData;
import pet.park.service.ParkService;

@RestController
@RequestMapping("/pet_park")
@Slf4j
public class ParkController {
	@Autowired
	private ParkService parkService;

	@PostMapping("/contributor")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ContributorData insertContributor(@RequestBody ContributorData contributorData) {
		log.info("Creating contributor {}", contributorData);
		return parkService.saveContributor(contributorData);
	}

	@PutMapping("/contributor/{contributorId}")
	public ContributorData updateContributor(@PathVariable Long contributorId,
			@RequestBody ContributorData contributorData) {
		contributorData.setContributorId(contributorId);
		log.info("Updating contributor {}", contributorData);
		return parkService.saveContributor(contributorData);
	} // (VIDEO 14-4 Update Controller) -> method to update a contributor

	@GetMapping("/contributor")
	public List<ContributorData> retriveAllContributors() {
		log.info("Retrieve all contributors called.");
		return parkService.retrieveAllContributors();
	} // (VIDEO 14-2 Retrieve contributors) -> method to return all contributors

	@GetMapping("/contributor/{contributorId}")
	public ContributorData retrieveContributorById(@PathVariable Long contributorId) {
		log.info("Retrieving contributor with ID={}", contributorId);
		return parkService.retrieveContributorById(contributorId);
	} // (VIDEO 14-2 Retrieve contributors) -> method to retrieve one contributor at a
		// time by ID

	@DeleteMapping("/contributor/{contributorId}")
	public Map<String, String> deleteContributorById(@PathVariable Long contributorId) {
		log.info("Deleting contributor with ID =", contributorId);

		parkService.deleteContributorById(contributorId);

		return Map.of("message", "Deletion of contributor with ID=" + contributorId + " was successful.");
	} // (VIDEO 14-5 Delete Contributor) Delete one at a time by ID

	@DeleteMapping("/contributor")
	public void deleteAllContributors() {
		log.info("Attempting to delete all contributors");
		throw new UnsupportedOperationException("Deleting all contributors is not allowed.");
	} // (VIDEO 14-5 Delete Contributor) -> Preventing user from deleting all
		// contributors

	@PostMapping("/contributor/{contributorId}/park")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetParkData insertPetPark(@PathVariable Long contributorId, @RequestBody PetParkData petParkData) {
		log.info("Creating park {} for contributor wih ID={}", petParkData, contributorId);

		return parkService.savePetPark(contributorId, petParkData);
	} // (14-6 Create Location VIDEO) controller method for insertPetPark

	@PutMapping("/contributor/{contributorId}/park/{parkId}")
	public PetParkData updatePetPark(@PathVariable Long contributorId, @PathVariable Long parkId,
			@RequestBody PetParkData petParkData) {

		petParkData.setPetParkId(parkId);
		log.info("Creating park {} for contributor wih ID={}", petParkData, contributorId);

		return parkService.savePetPark(contributorId, petParkData);

	} // (14-6 Create Location VIDEO) controller method for update pet park

	@GetMapping("/contributor/{contributorId}/park/{parkId}")
	public PetParkData retrievePetParkById(@PathVariable Long contributorId, @PathVariable Long parkId) {
		log.info("Retrieving pet park with ID={} for contributor with ID={}", parkId, contributorId);

		return parkService.retrievePetParkById(contributorId, parkId);

	}

}
