package com.example.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.model.Snap;
import com.example.api.repository.SnapRepository;

import lombok.Data;

/** SnapService
 *  provide an interface used by the snap controller to access snap objects store in
 *  the database
 *  @apiNote contains:
 *  getSnap - @return an Optional<Snap>     - get 1 snap if exist
 *  getSnaps - @return an Iterable<Snap>    - get all the employe store in the db
 *  deleteSnap - @return void               - delete 1 snap if exist
 *  saveSnap - @return snap                 - insert or update 1 snap
 */
@Data // create the Setter and the Getter of all object declared in the class
@Service // define this bean as a service bean
public class SnapService {
    
    @Autowired // connect to the bean instance of SnapRepository (auto instanciate by Spring)
    private SnapRepository snapRepository;

    /** get 1 snap if exist
     * @param id
     * @return
     */
    public Optional<Snap> getSnap(final Long id) {
        return snapRepository.findById(id);
    }

    /** get all the snaps
     * @return
     */
    public Iterable<Snap> getSnaps() {
        return snapRepository.findAll();
    }


    /** insert or update 1 snap
     * @param snap
     * @return
     */
    public Snap saveSnap(Snap snap) {
        return snapRepository.save(snap);
    }

    /** delete 1 snap if exist
     * @param id
     */
    public void deleteSnap(final Long id) {
        snapRepository.deleteById(id);
    }

}
