DROP TABLE vozidlo;

CREATE TABLE vozidlo (
	vyrobce varchar2(64),
	model varchar2(64),
	foto ORDSYS.ORDImage,
	foto_si ORDSYS.SI_StillImage,
	foto_ac ORDSYS.SI_AverageColor,
	foto_ch ORDSYS.SI_ColorHistogram,
	foto_pc ORDSYS.SI_PositionalColor,
	foto_tx ORDSYS.SI_Texture,
	primary key (vyrobce, model)
);
