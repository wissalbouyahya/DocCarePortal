export class Medecin {
    constructor(
        public idMedecin?: number,
        public nom?: string,
        public prenom?: string,
        public email?: string,
        public mdp?: string,
        public specialite?: string,
        public adresseM?: string,
        public numeroTel?: string,   
        public status ?: string ,
        public etat ?:boolean
        
    ) {}
}
