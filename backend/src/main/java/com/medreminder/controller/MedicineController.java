package com.medreminder.controller;

import com.medreminder.model.Medicine;
import com.medreminder.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin("*")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @PostMapping("/add")
    public ResponseEntity<?> addMedicine(
            @NonNull @RequestParam("userId") Long userId,
            @RequestParam("name") String name,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "aiSafe", required = false) Boolean aiSafe,
            @RequestParam(value = "aiDosage", required = false) String aiDosage,
            @RequestParam(value = "aiTiming", required = false) String aiTiming,
            @RequestParam(value = "aiWarning", required = false) String aiWarning) {
        try {
            Medicine medicine = medicineService.addMedicine(userId, name, image, aiSafe, aiDosage, aiTiming, aiWarning);
            return ResponseEntity.ok(medicine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<Medicine>> getMedicines(@NonNull @PathVariable Long userId) {
        return ResponseEntity.ok(medicineService.getUserMedicines(userId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMedicine(@NonNull @PathVariable Long id) {
        try {
            medicineService.deleteMedicine(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting medicine: " + e.getMessage());
        }
    }
}
