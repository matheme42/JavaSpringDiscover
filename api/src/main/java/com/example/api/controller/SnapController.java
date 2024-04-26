package com.example.api.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.model.database.Snap;
import com.example.api.service.SnapService;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * SnapController is a RestController / Received incoming request:
 * @apiNote this controller managed:
 *  POST /snap - Create an Snap
 *  GET /snaps - get all available snap
 * 
 *  GET /snap/id - get the specified snap or null if doesn't exist
 *  PUT /snap/id - update the specified snap if exist
 *  DELETE /snap/id - delete the specified snap if exist
 * 
 * @service used SnapService
 */

@RestController
public class SnapController {
    @Autowired // connect to the bean instance of SnapService  (auto instanciate by Spring)
    private SnapService snapService;

    /**
     * @param snap
     * @return - the snap after saving in the database
     */
    @PostMapping("/snap")
    public Snap createSnaps(@RequestBody Snap snap) {
        return snapService.saveSnap(snap);
    }


    /**
    * Read - Get all snaps
    * @return - An Iterable object of Snap full filled
    */
    @GetMapping("/snaps")
    public Iterable<Snap> getSnaps() {
        return snapService.getSnaps();
    }

    /**
     * @param id The id of the snap
     * @return - the snap corresponding to the id if exist it exist in the database
     */
    @GetMapping("/snap/{id}")
    public Snap saveSnaps(@PathVariable("id") final Long id) {
        Optional<Snap> snap = snapService.getSnap(id);
        return snap.isPresent() ? snap.get() : null;
    }

    /**
	 * Update - Update an existing snap
	 * @param id - The id of the snap to update
	 * @param snap - The snap object updated
	 * @return - the snap updated can be null
	 */
    @PutMapping("snap/{id}")
    public Snap putMethodName(@PathVariable("id") final Long id, @RequestBody Snap updatedSnap) {

        Optional<Snap> e = snapService.getSnap(id);
        if (!e.isPresent()) return null;
        Snap snap = e.get();

        // update the title if exist
        String title = updatedSnap.getTitle();
        if (title != null) snap.setTitle(title);

        // update the description if exist
        String description = updatedSnap.getDescription();
        if (description != null) snap.setDescription(description);

        // update the snapNumber if exist
        Long snapNumber = updatedSnap.getSnap();
        if (snapNumber != null) snap.setSnap(snapNumber);

        // update the imageUrl if exist
        String imageUrl = updatedSnap.getImageUrl();
        if (imageUrl != null) snap.setImageUrl(imageUrl);

        // update the location if exist
        String location = updatedSnap.getLocation();
        if (location != null) snap.setLocation(location);

        // update the creationDate if exist
        Date creationDate = updatedSnap.getCreatedDate();
        if (creationDate != null) snap.setCreatedDate(creationDate);

        snapService.saveSnap(snap);
        return snap; // return the updatedSnap;
    }

    /**
     * @param id The id of the snap
     * delete the snap corresponding to the id
     */
    @DeleteMapping("/snap/{id}")
    public void deleteSnaps(@PathVariable("id") final long id) {
        snapService.deleteSnap(id);
    }
}
