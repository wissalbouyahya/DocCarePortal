import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';
import { Medecin } from '../Entity/medecin.Entity';
import { CrudService } from '../service/crud.service';

@Component({
  selector: 'app-modifiermedecin',
  templateUrl: './modifiermedecin.component.html',
  styleUrls: ['./modifiermedecin.component.css']
})
export class ModifiermedecinComponent implements OnInit {
  id: number;
  medecinForm: FormGroup;

  constructor(
    private service: CrudService,
    private router: Router,
    private fb: FormBuilder,
    private rout: ActivatedRoute
  ) {
    
    let formControls = {
      nom: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.pattern("^[a-zA-ZÀ-ÿ\\s'-]+$")
      ]),
      prenom: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
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
      specialite: new FormControl('', [
        Validators.required,
        Validators.minLength(2)
      ]),
      
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
    };
    this.medecinForm = this.fb.group(formControls);
  }

  get nom() { return this.medecinForm.get('nom'); }
  get prenom() { return this.medecinForm.get('prenom'); }
  get email() { return this.medecinForm.get('email'); }
  get mdp() { return this.medecinForm.get('mdp'); }
  get specialite() { return this.medecinForm.get('specialite'); }
  get adresseM() { return this.medecinForm.get('adresseM'); }     
  get numeroTel() { return this.medecinForm.get('numeroTel'); }   
  get status() { return this.medecinForm.get('status'); }          

  
  getErrorMessage(control: any, fieldName: string) {
    if (control?.hasError('required')) {
      return `Le champ ${fieldName} est obligatoire`;
    }
    if (control?.hasError('minlength')) {
      return `Le champ ${fieldName} doit contenir au moins ${control.errors?.minlength?.requiredLength} caractères`;
    }
    if (control?.hasError('email')) {
      return 'Format d\'email invalide';
    }
    if (control?.hasError('pattern')) {
      if (fieldName === 'nom' || fieldName === 'prenom') {
        return 'Seuls les caractères alphabétiques sont autorisés';
      }
      if (fieldName === 'numeroTel') {
        return 'Le numéro de téléphone ne doit contenir que des chiffres';
      }
      return 'Format invalide';
    }
    return '';
  }

  ngOnInit(): void {
    
    let idEvent = this.rout.snapshot.params['id'];
    this.id = idEvent;
    
    
    this.service.findMedecinById(idEvent).subscribe({
      next: (result: any) => {
        let medecin = result;
        console.log('Médecin chargé:', medecin);
        
       
        this.medecinForm.patchValue({
          nom: medecin.nom,
          prenom: medecin.prenom,
          email: medecin.email,
          mdp: medecin.mdp,
          specialite: medecin.specialite,
          adresseM: medecin.adresseM,        
          numeroTel: medecin.numeroTel,       
          status: medecin.status || 'ACTIF'   
        });
      },
      error: (err: any) => {
        console.error('Erreur lors du chargement du médecin:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger les informations du médecin'
        });
      }
    });
  }

  
  updateMedecin() {
    
    if (this.medecinForm.invalid) {
      let errorMessages: string[] = [];
      Object.keys(this.medecinForm.controls).forEach(key => {
        const control = this.medecinForm.get(key);
        if (control?.invalid) {
          errorMessages.push(this.getErrorMessage(control, key));
        }
      });

      Swal.fire({
        icon: 'error',
        title: 'Champs invalides',
        html: errorMessages.join('<br>'),
        showConfirmButton: true
      });
      return;
    }

    
    let data = this.medecinForm.value;
    
    
    let medecin = new Medecin(
      this.id,
      data.nom,
      data.prenom,
      data.email,
      data.mdp,
      data.specialite,
      data.adresseM,     
      data.numeroTel,      
      data.status          
    );

    console.log('Médecin à modifier:', medecin);

    this.service.updateMedecin(this.id, medecin).subscribe({
      next: (res: any) => {
        console.log('Mise à jour réussie:', res);
        Swal.fire({
          icon: 'success',
          title: 'Succès',
          text: 'Médecin modifié avec succès !',
          timer: 2000,
          showConfirmButton: false
        }).then(() => {
          this.router.navigate(['/listemedecin']);
        });
      },
      error: (err: any) => {
        console.error('Erreur lors de la modification:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Une erreur est survenue lors de la modification du médecin: ' + 
                (err.error?.message || err.message || 'Erreur inconnue')
        });
      }
    });
  }
}
