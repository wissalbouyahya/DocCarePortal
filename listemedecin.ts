import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { Medecin } from '../Entity/medecin.Entity';
import { CrudService } from '../service/crud.service';

@Component({
  selector: 'app-listemedecin',
  templateUrl: './listemedecin.component.html',
  styleUrls: ['./listemedecin.component.css']
})
export class ListemedecinComponent implements OnInit {
  listeMedecin: Medecin[] = [];

  constructor(
    private service: CrudService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chargerMedecins();
  }

  chargerMedecins() {
    this.service.getMedecin().subscribe({
      next: (medecins) => {
        this.listeMedecin = medecins;
        console.log('Médecins chargés:', medecins);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des médecins:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger la liste des médecins'
        });
      }
    });
  }


  DeleteMedecin(medecin: Medecin) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: `Voulez-vous vraiment supprimer le médecin "${medecin.nom} ${medecin.prenom}" ?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        
        this.service.onDeleteMedecin(medecin.idMedecin).subscribe({
          next: () => {
            // Mettre à jour la liste locale
            this.listeMedecin = this.listeMedecin.filter(m => m.idMedecin !== medecin.idMedecin);
            
            Swal.fire({
              icon: 'success',
              title: 'Supprimé !',
              text: 'Le médecin a été supprimé avec succès.',
              timer: 2000,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error('Erreur lors de la suppression:', err);
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Une erreur est survenue lors de la suppression du médecin.'
            });
          }
        });
      }
    });
  }

  updateMedecinStatus(medecin: Medecin): void {
    const nouveauStatus = medecin.status === 'ACTIF' ? 'INACTIF' : 'ACTIF';
    const action = nouveauStatus === 'ACTIF' ? 'activer' : 'désactiver';

    Swal.fire({
      title: `Êtes-vous sûr ?`,
      text: `Voulez-vous vraiment ${action} le médecin "${medecin.nom} ${medecin.prenom}" ?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: nouveauStatus === 'ACTIF' ? '#28a745' : '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: `Oui, ${action}`,
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
       
        const ancienStatus = medecin.status;
        medecin.status = nouveauStatus;
        
        
        this.service.updateMedecinEtat(medecin.idMedecin, medecin).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Succès !',
              text: `Le médecin a été ${action} avec succès.`,
              timer: 2000,
              showConfirmButton: false
            });
            this.chargerMedecins(); 
          },
          error: (err) => {
            console.error('Erreur lors de la mise à jour:', err);
            
            medecin.status = ancienStatus;
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: `Une erreur est survenue lors de la ${action} du médecin.`
            });
          }
        });
      }
    });
  }

 
  rafraichirListe(): void {
    this.chargerMedecins();
  }
}
