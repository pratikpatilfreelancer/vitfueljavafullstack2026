-- VANGUARD database schema
-- Run this in MySQL before using the JDBC (IncidentDAO/ResourceDAO) parts
-- of the project. Everything else in the project runs fine WITHOUT this
-- being set up, since the in-memory DispatchCenter is the default.

CREATE DATABASE IF NOT EXISTS vanguard_db;
USE vanguard_db;

DROP TABLE IF EXISTS incidents;
DROP TABLE IF EXISTS resources;

CREATE TABLE resources (
    id      VARCHAR(20)  NOT NULL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    type    ENUM('FIRE','MEDICAL','FLOOD','STRUCTURAL','OTHER') NOT NULL,
    x       INT NOT NULL,
    y       INT NOT NULL,
    status  ENUM('AVAILABLE','BUSY') NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE incidents (
    id                    VARCHAR(20)  NOT NULL PRIMARY KEY,
    description           VARCHAR(500) NOT NULL,
    x                     INT NOT NULL,
    y                     INT NOT NULL,
    type                  ENUM('FIRE','MEDICAL','FLOOD','STRUCTURAL','OTHER') NOT NULL,
    severity              ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
    confidence            INT NOT NULL DEFAULT 0,
    status                ENUM('REPORTED','ASSIGNED','RESOLVED') NOT NULL DEFAULT 'REPORTED',
    assigned_resource_id  VARCHAR(20),
    reported_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assigned_resource
        FOREIGN KEY (assigned_resource_id) REFERENCES resources(id)
);

-- Sample seed data
INSERT INTO resources (id, name, type, x, y) VALUES
 ('r1', 'Engine 12', 'FIRE', 60, 60),
 ('r2', 'Medic 3', 'MEDICAL', 120, 90),
 ('r3', 'Rescue Alpha', 'STRUCTURAL', 200, 130),
 ('r4', 'Flood Boat 2', 'FLOOD', 340, 40),
 ('r5', 'Volunteer Unit A', 'OTHER', 180, 220);

-- Reference incident patterns: a small labeled "case base" the
-- classifier compares new reports against (see
-- service/DatabaseBackedClassifier.java). Each row is a past incident
-- with its confirmed correct type/severity.
CREATE TABLE incident_patterns (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    type        ENUM('FIRE','MEDICAL','FLOOD','STRUCTURAL','OTHER') NOT NULL,
    severity    ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL
);

INSERT INTO incident_patterns (description, type, severity) VALUES
 ('Multi-story building fire spreading fast, residents trapped on upper floors', 'FIRE', 'CRITICAL'),
 ('Small kitchen fire in restaurant, contained but smoke spreading to neighbors', 'FIRE', 'MEDIUM'),
 ('Electrical fire in server room, no injuries, fire suppression activated', 'FIRE', 'MEDIUM'),
 ('Wildfire approaching residential area, multiple homes threatened', 'FIRE', 'CRITICAL'),
 ('Car engine fire on highway shoulder, driver safely out of vehicle', 'FIRE', 'LOW'),
 ('Elderly resident having chest pains, needs urgent medical help', 'MEDICAL', 'HIGH'),
 ('Person unconscious after collapsing at gym, bystanders performing CPR', 'MEDICAL', 'CRITICAL'),
 ('Minor cut on hand from kitchen accident, bleeding controlled', 'MEDICAL', 'LOW'),
 ('Multiple people injured in traffic collision, some unconscious', 'MEDICAL', 'CRITICAL'),
 ('Allergic reaction, difficulty breathing, needs immediate attention', 'MEDICAL', 'HIGH'),
 ('Flash flooding on Main Street, several cars stranded with people inside', 'FLOOD', 'CRITICAL'),
 ('Basement flooding after heavy rain, no injuries, water rising slowly', 'FLOOD', 'MEDIUM'),
 ('River overflowing banks, evacuation advised for low-lying homes', 'FLOOD', 'HIGH'),
 ('Minor street ponding after storm, no vehicles or people affected', 'FLOOD', 'LOW'),
 ('Apartment building collapse after gas explosion, multiple people trapped', 'STRUCTURAL', 'CRITICAL'),
 ('Balcony partially collapsed, structure unstable, area cordoned off', 'STRUCTURAL', 'HIGH'),
 ('Old bridge showing cracks, inspectors on site, no immediate danger', 'STRUCTURAL', 'MEDIUM'),
 ('Scaffolding collapse at construction site, one worker injured', 'STRUCTURAL', 'HIGH'),
 ('Landslide blocking highway, one vehicle partially buried', 'STRUCTURAL', 'HIGH'),
 ('Power lines down near school, sparking, area needs to be cordoned off', 'OTHER', 'MEDIUM'),
 ('Minor road blockage from fallen tree, no injuries reported', 'OTHER', 'LOW'),
 ('Large public gathering becoming unruly, crowd control requested', 'OTHER', 'MEDIUM'),
 ('Gas leak reported near residential block, strong smell, evacuating area', 'OTHER', 'HIGH');

-- Keyword dataset: individual words/phrases mapped to an incident type
-- and a severity weight. This is what SqlKeywordClassifier queries —
-- adding or tuning a keyword is just an INSERT/UPDATE here, no Java
-- code changes or recompiling needed. type is NULL for keywords that
-- only affect severity (e.g. "multiple", "minor") without implying a
-- specific incident type.
CREATE TABLE keyword_rules (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    keyword         VARCHAR(50) NOT NULL,
    type            ENUM('FIRE','MEDICAL','FLOOD','STRUCTURAL','OTHER') NULL,
    severity_weight INT NOT NULL DEFAULT 0
);

INSERT INTO keyword_rules (keyword, type, severity_weight) VALUES
 ('fire', 'FIRE', 25),
 ('smoke', 'FIRE', 15),
 ('burn', 'FIRE', 20),
 ('burning', 'FIRE', 20),
 ('blaze', 'FIRE', 20),
 ('wildfire', 'FIRE', 25),
 ('flood', 'FLOOD', 20),
 ('flooding', 'FLOOD', 20),
 ('water', 'FLOOD', 10),
 ('drown', 'FLOOD', 25),
 ('drowning', 'FLOOD', 25),
 ('stranded', 'FLOOD', 15),
 ('submerged', 'FLOOD', 20),
 ('collapse', 'STRUCTURAL', 30),
 ('collapsed', 'STRUCTURAL', 30),
 ('trapped', 'STRUCTURAL', 30),
 ('debris', 'STRUCTURAL', 20),
 ('rubble', 'STRUCTURAL', 20),
 ('landslide', 'STRUCTURAL', 25),
 ('scaffolding', 'STRUCTURAL', 15),
 ('injured', 'MEDICAL', 20),
 ('injury', 'MEDICAL', 15),
 ('unconscious', 'MEDICAL', 30),
 ('bleeding', 'MEDICAL', 25),
 ('chest', 'MEDICAL', 20),
 ('pain', 'MEDICAL', 15),
 ('breathing', 'MEDICAL', 20),
 ('allergic', 'MEDICAL', 15),
 ('multiple', NULL, 20),
 ('several', NULL, 15),
 ('many', NULL, 15),
 ('critical', NULL, 25),
 ('dying', NULL, 30),
 ('spreading', NULL, 20),
 ('minor', NULL, -20),
 ('small', NULL, -10),
 ('contained', NULL, -15);
