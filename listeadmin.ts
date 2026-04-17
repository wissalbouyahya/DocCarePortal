import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { Admin } from '../Entity/Admin.Entity';
import { CrudService } from '../service/crud.service';

@Component({
  selector: 'app-listeadmin',
  templateUrl: './listeadmin.component.html',
  styleUrls: ['./listeadmin.component.css']
})
export class ListeadminComponent implements OnInit {

  listeAdmin: Admin[] = [];
  role: string = '';

  constructor(private service: CrudService, private router: Router) {}

  ngOnInit(): void {
    // Récupérer le rôle depuis le localStorage
    this.role = localStorage.getItem("role") as string;
    this.chargerAdmins();
  }

  // Charger la liste des admins
  chargerAdmins() {
    this.service.getAdmin().subscribe({
      next: (admins) => {
        this.listeAdmin = admins;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des administrateurs:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger la liste des administrateurs'
        });
      }
    });
  }

  // Supprimer un admin
  DeleteAdmin(admin: Admin) {

    if (admin.role === 'superadmin') {
      Swal.fire({
        icon: 'error',
        title: 'Action non autorisée',
        text: 'Impossible de supprimer un super administrateur'
      });
      return;
    }

    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: `Voulez-vous vraiment supprimer l'administrateur "${admin.nom} ${admin.prenom}" ?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {

      if (result.isConfirmed) {

        this.service.onDeleteAdmin(admin.id).subscribe({
          next: () => {
            // Retirer l'admin supprimé de la liste locale
            this.listeAdmin = this.listeAdmin.filter(a => a.id !== admin.id);

            Swal.fire({
              icon: 'success',
              title: 'Supprimé !',
              text: 'L\'administrateur a été supprimé avec succès.',
              timer: 2000,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error('Erreur lors de la suppression:', err);
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Une erreur est survenue lors de la suppression de l\'administrateur.'
            });
          }
        });

      }
    });
  }

  
  editAdmin(admin: Admin) {
    this.router.navigate(['/modifier-admin', admin.id]);
  }

}
