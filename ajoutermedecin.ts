import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { CrudService } from '../service/crud.service';
import { Medecin } from '../Entity/medecin.Entity';

@Component({
  selector: 'app-ajoutermedecin',
  templateUrl: './ajoutermedecin.component.html',
  styleUrls: ['./ajoutermedecin.component.css']
})
export class AjoutermedecinComponent implements OnInit {

  medecinForm!: FormGroup;

  constructor(
    private services: CrudService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.medecinForm = this.fb.group({
      nom: new FormControl('', [
        Validators.required,
        Validators.minLength(3),
        Validators.pattern("^[a-zA-ZÀ-ÿ\\s'-]+$")
      ]),
      prenom: new FormControl('', [
        Validators.required,
        Validators.minLength(3),
        Validators.pattern("^[a-zA-ZÀ-ÿ\\s'-]+$")
      ]),
      email: new FormControl('', [
        Validators.required,
        Validators.email
      ]),
      mdp: new FormControl('', [
        Validators.required,
        Validators.minLength(6)
      ]),
      specialite: new FormControl('', Validators.required),
      adresseM: new FormControl('', [       
        Validators.required,
        Validators.minLength(5)
      ]),
      numeroTel: new FormControl('', [      
        Validators.required,
        Validators.minLength(8),
        Validators.pattern("^[0-9]+$")
      ]),
      status: new FormControl('ACTIF', [     
        Validators.required
      ])
    });
  }

  
  get nom() { return this.medecinForm.get('nom'); }
  get prenom() { return this.medecinForm.get('prenom'); }
  get email() { return this.medecinForm.get('email'); }
  get mdp() { return this.medecinForm.get('mdp'); }
  get specialite() { return this.medecinForm.get('specialite'); }
  get adresseM() { return this.medecinForm.get('adresseM'); }
  get numeroTel() { return this.medecinForm.get('numeroTel'); }
  get status() { return this.medecinForm.get('status'); }

  
  onReset(): void {
    this.medecinForm.reset({
      status: 'ACTIF'  
    });
  }

  
  addNewMedecin(): void {
    if (this.medecinForm.invalid) {
     
      let errorMessages: string[] = [];
      Object.keys(this.medecinForm.controls).forEach(key => {
        const control = this.medecinForm.get(key);
        if (control?.invalid) {
          if (control.hasError('required')) {
            errorMessages.push(`Le champ ${key} est requis`);
          } else if (control.hasError('minlength')) {
            errorMessages.push(`Le champ ${key} doit contenir au moins ${control.errors?.['minlength']?.requiredLength} caractères`);
          } else if (control.hasError('email')) {
            errorMessages.push(`Format d'email invalide`);
          } else if (control.hasError('pattern')) {
            if (key === 'numeroTel') {
              errorMessages.push(`Le numéro de téléphone ne doit contenir que des chiffres`);
            } else {
              errorMessages.push(`Le champ ${key} contient des caractères invalides`);
            }
          }
        }
      });

      Swal.fire({
        icon: 'error',
        title: 'Formulaire invalide',
        html: errorMessages.join('<br>')
      });
      return;
    }

    const data = this.medecinForm.value;

    const medecin = new Medecin(
      null,                     
      data.nom,                
      data.prenom,             
      data.email,               
      data.mdp,                 
      data.specialite,         
      data.adresseM,            
      data.numeroTel,           
      data.status               
    );

    console.log('Médecin envoyé :', medecin);

    this.services.addmedecin(medecin).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Succès',
          text: 'Médecin ajouté avec succès',
          timer: 2000,
          showConfirmButton: false
        }).then(() => {
          this.router.navigate(['/listemedecin']);
        });
      },
      error: (err) => {
        console.error('Erreur détaillée:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Erreur lors de l\'ajout du médecin: ' + 
                (err.error?.message || err.message || 'Erreur inconnue')
        });
      }
    });
  }
}
