INSERT INTO categories (name)
SELECT seed.name
FROM (VALUES
          ('Pasta'),
          ('Salade'),
          ('Aziatisch'),
          ('Ontbijt'),
          ('Lunch'),
          ('Diner'),
          ('Vegetarisch'),
          ('Soep'),
          ('Vis'),
          ('Kip'),
          ('Snel'),
          ('Gezond'),
          ('Mexicaans'),
          ('Italiaans')
     ) AS seed(name)
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.name = seed.name);

INSERT INTO ingredients (name)
SELECT seed.name
FROM (VALUES
          ('Pasta'),
          ('Rijst'),
          ('Aardappelen'),
          ('Tomaat'),
          ('Cherrytomaten'),
          ('Komkommer'),
          ('Paprika'),
          ('Ui'),
          ('Rode ui'),
          ('Knoflook'),
          ('Wortel'),
          ('Courgette'),
          ('Broccoli'),
          ('Spinazie'),
          ('Champignons'),
          ('Sla'),
          ('Kipfilet'),
          ('Gehakt'),
          ('Zalm'),
          ('Ei'),
          ('Melk'),
          ('Room'),
          ('Geraspte kaas'),
          ('Parmezaanse kaas'),
          ('Mozzarella'),
          ('Olijfolie'),
          ('Boter'),
          ('Bloem'),
          ('Suiker'),
          ('Zout'),
          ('Peper'),
          ('Paprikapoeder'),
          ('Kerriepoeder'),
          ('Oregano'),
          ('Basilicum'),
          ('Tomatenpuree'),
          ('Tomatenblokjes'),
          ('Kokosmelk'),
          ('Sojasaus'),
          ('Citroen'),
          ('Wraps'),
          ('Risottorijst'),
          ('Bouillon'),
          ('Lasagnebladen')
     ) AS seed(name)
WHERE NOT EXISTS (SELECT 1 FROM ingredients i WHERE i.name = seed.name);

INSERT INTO users (email, keycloak_id)
SELECT seed.email, seed.keycloak_id
FROM (VALUES
          ('user@user.com', '304d1841-1c7b-46fe-bffd-04b3d2dc50cc'),
          ('user2@user2.com', 'e28cfe45-8dec-418e-89ff-e2b837c2a395'),
          ('admin@admin.nl', '2d42a3a7-6cfe-4fd4-a5a0-3d30b294665d')
     ) AS seed(email, keycloak_id)
WHERE NOT EXISTS (
    SELECT 1 FROM users u WHERE u.email = seed.email OR u.keycloak_id = seed.keycloak_id
);

INSERT INTO profiles (username, user_id)
SELECT seed.username, u.id
FROM (VALUES
          ('user@user.com', 'Kookliefhebber'),
          ('user2@user2.com', 'Keukenprinses'),
          ('admin@admin.nl', 'chefadmin')
     ) AS seed(email, username)
         JOIN users u ON u.email = seed.email
WHERE NOT EXISTS (SELECT 1 FROM profiles p WHERE p.user_id = u.id);

INSERT INTO recipes (title, description, instructions, servings, preparation_time, cooking_time, difficulty, owner_profile_id)
SELECT seed.title, seed.description, seed.instructions, seed.servings, seed.preparation_time, seed.cooking_time, seed.difficulty, p.id
FROM (VALUES
          ('Pasta met spinazie en tomaat', 'Een eenvoudige pasta met verse spinazie en tomaat.', 'Kook de pasta. Bak de knoflook en tomaten. Voeg de spinazie toe en meng alles met de pasta.', 2, 10, 15, 'EASY', 'user@user.com'),
          ('Omelet met groenten', 'Een luchtige omelet met verse groenten.', 'Snijd de groenten. Klop de eieren los. Bak de groenten kort in een pan. Voeg de eieren toe en bak de omelet gaar.', 2, 5, 10, 'EASY', 'user@user.com'),
          ('Vegetarische tomatensoep', 'Een heerlijke zelfgemaakte tomatensoep met verse groenten.', 'Snijd de tomaten, ui en knoflook. Fruit de ui en knoflook. Voeg de tomaten en water toe. Laat de soep koken en pureer tot een glad geheel.', 4, 10, 25, 'EASY', 'user@user.com'),
          ('Zalm met aardappelen', 'Gebakken zalm met aardappelen en broccoli.', 'Schil en kook de aardappelen. Kook de broccoli beetgaar. Bak de zalm in olijfolie en serveer met de groenten.', 2, 15, 30, 'MEDIUM', 'user@user.com'),
          ('Kip met rijst en broccoli', 'Een gezonde maaltijd met kipfilet, rijst en broccoli.', 'Kook de rijst volgens de verpakking. Snijd de kipfilet in stukjes en bak deze met kruiden. Kook de broccoli beetgaar en serveer alles samen.', 2, 15, 20, 'MEDIUM', 'user2@user2.com'),
          ('Mexicaanse wraps', 'Gevulde wraps met gehakt, paprika en tomaat.', 'Snijd de paprika en ui. Bak het gehakt rul en voeg de groenten toe. Breng op smaak met kruiden en vul de wraps met het mengsel.', 4, 15, 20, 'EASY', 'user2@user2.com'),
          ('Risotto met champignons', 'Een romige risotto met gebakken champignons en Parmezaanse kaas.', 'Snijd de champignons en ui. Fruit de ui en voeg de risottorijst toe. Voeg beetje bij beetje warme bouillon toe en blijf roeren. Bak de champignons apart en meng ze met de risotto. Voeg de Parmezaanse kaas toe.', 2, 15, 30, 'HARD', 'user2@user2.com'),
          ('Salade met mozzarella', 'Een frisse salade met tomaat, mozzarella en basilicum.', 'Was de groenten en snijd de tomaten en mozzarella. Verdeel de sla over de borden en voeg de tomaat en mozzarella toe. Besprenkel met olijfolie en garneer met basilicum.', 2, 10, 0, 'EASY', 'user2@user2.com'),
          ('Lasagne uit de oven', 'Een klassieke lasagne met gehakt, tomatensaus en kaas.', 'Verwarm de oven voor op 180 graden. Bak het gehakt met ui en knoflook. Voeg tomatenblokjes toe en laat de saus sudderen. Maak laagjes van saus, lasagnebladen en kaas. Bak de lasagne 35 minuten in de oven.', 4, 25, 35, 'HARD', 'admin@admin.nl'),
          ('Romige pasta met zalm', 'Een heerlijke pasta met zalm, spinazie en romige saus.', 'Kook de pasta volgens de verpakking. Bak de zalm in een pan. Voeg spinazie en room toe en laat de saus kort inkoken. Meng de pasta met de saus en serveer met de zalm.', 2, 10, 20, 'MEDIUM', 'admin@admin.nl'),
          ('Groentecurry met kokosmelk', 'Een kruidige vegetarische curry met groenten en kokosmelk.', 'Snijd de groenten in stukjes. Fruit de ui en knoflook met kerriepoeder. Voeg de groenten en kokosmelk toe. Laat de curry sudderen totdat de groenten gaar zijn. Serveer met rijst.', 4, 15, 25, 'MEDIUM', 'admin@admin.nl'),
          ('Pannenkoeken', 'Zelfgemaakte pannenkoeken met melk, bloem en eieren.', 'Meng de bloem, melk en eieren tot een glad beslag. Verhit boter in een koekenpan. Giet een dun laagje beslag in de pan en bak de pannenkoek aan beide kanten goudbruin.', 4, 10, 20, 'EASY', 'admin@admin.nl')
     ) AS seed(title, description, instructions, servings, preparation_time, cooking_time, difficulty, owner_email)
         JOIN users u ON u.email = seed.owner_email
         JOIN profiles p ON p.user_id = u.id
WHERE NOT EXISTS (
    SELECT 1 FROM recipes r WHERE r.title = seed.title AND r.owner_profile_id = p.id
);

INSERT INTO recipe_ingredients (recipe_id, ingredient_id, amount, unit)
SELECT r.id, i.id, seed.amount, seed.unit
FROM (VALUES
          ('Pasta met spinazie en tomaat', 'user@user.com', 'Pasta', 200, 'g'),
          ('Pasta met spinazie en tomaat', 'user@user.com', 'Spinazie', 150, 'g'),
          ('Pasta met spinazie en tomaat', 'user@user.com', 'Tomaat', 2, 'stuks'),
          ('Pasta met spinazie en tomaat', 'user@user.com', 'Knoflook', 2, 'teentjes'),
          ('Pasta met spinazie en tomaat', 'user@user.com', 'Olijfolie', 1, 'eetlepel'),
          ('Omelet met groenten', 'user@user.com', 'Ei', 4, 'stuks'),
          ('Omelet met groenten', 'user@user.com', 'Paprika', 1, 'stuks'),
          ('Omelet met groenten', 'user@user.com', 'Spinazie', 75, 'g'),
          ('Omelet met groenten', 'user@user.com', 'Melk', 30, 'ml'),
          ('Omelet met groenten', 'user@user.com', 'Boter', 10, 'g'),
          ('Vegetarische tomatensoep', 'user@user.com', 'Tomaat', 800, 'g'),
          ('Vegetarische tomatensoep', 'user@user.com', 'Ui', 1, 'stuks'),
          ('Vegetarische tomatensoep', 'user@user.com', 'Knoflook', 2, 'teentjes'),
          ('Vegetarische tomatensoep', 'user@user.com', 'Olijfolie', 1, 'eetlepel'),
          ('Vegetarische tomatensoep', 'user@user.com', 'Basilicum', 1, 'theelepel'),
          ('Zalm met aardappelen', 'user@user.com', 'Zalm', 300, 'g'),
          ('Zalm met aardappelen', 'user@user.com', 'Aardappelen', 400, 'g'),
          ('Zalm met aardappelen', 'user@user.com', 'Broccoli', 300, 'g'),
          ('Zalm met aardappelen', 'user@user.com', 'Olijfolie', 1, 'eetlepel'),
          ('Zalm met aardappelen', 'user@user.com', 'Citroen', 1, 'stuks'),
          ('Kip met rijst en broccoli', 'user2@user2.com', 'Kipfilet', 300, 'g'),
          ('Kip met rijst en broccoli', 'user2@user2.com', 'Rijst', 150, 'g'),
          ('Kip met rijst en broccoli', 'user2@user2.com', 'Broccoli', 300, 'g'),
          ('Kip met rijst en broccoli', 'user2@user2.com', 'Olijfolie', 1, 'eetlepel'),
          ('Kip met rijst en broccoli', 'user2@user2.com', 'Paprikapoeder', 1, 'theelepel'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Wraps', 4, 'stuks'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Gehakt', 300, 'g'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Paprika', 2, 'stuks'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Ui', 1, 'stuks'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Tomaat', 2, 'stuks'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Paprikapoeder', 1, 'theelepel'),
          ('Risotto met champignons', 'user2@user2.com', 'Risottorijst', 180, 'g'),
          ('Risotto met champignons', 'user2@user2.com', 'Champignons', 250, 'g'),
          ('Risotto met champignons', 'user2@user2.com', 'Ui', 1, 'stuks'),
          ('Risotto met champignons', 'user2@user2.com', 'Bouillon', 600, 'ml'),
          ('Risotto met champignons', 'user2@user2.com', 'Parmezaanse kaas', 50, 'g'),
          ('Risotto met champignons', 'user2@user2.com', 'Boter', 20, 'g'),
          ('Salade met mozzarella', 'user2@user2.com', 'Sla', 100, 'g'),
          ('Salade met mozzarella', 'user2@user2.com', 'Tomaat', 2, 'stuks'),
          ('Salade met mozzarella', 'user2@user2.com', 'Mozzarella', 125, 'g'),
          ('Salade met mozzarella', 'user2@user2.com', 'Olijfolie', 1, 'eetlepel'),
          ('Salade met mozzarella', 'user2@user2.com', 'Basilicum', 5, 'g'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Lasagnebladen', 250, 'g'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Gehakt', 400, 'g'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Tomatenblokjes', 400, 'g'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Ui', 1, 'stuks'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Knoflook', 2, 'teentjes'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Geraspte kaas', 150, 'g'),
          ('Romige pasta met zalm', 'admin@admin.nl', 'Pasta', 200, 'g'),
          ('Romige pasta met zalm', 'admin@admin.nl', 'Zalm', 250, 'g'),
          ('Romige pasta met zalm', 'admin@admin.nl', 'Spinazie', 150, 'g'),
          ('Romige pasta met zalm', 'admin@admin.nl', 'Room', 150, 'ml'),
          ('Romige pasta met zalm', 'admin@admin.nl', 'Knoflook', 1, 'teentjes'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Rijst', 300, 'g'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Kokosmelk', 400, 'ml'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Courgette', 1, 'stuks'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Paprika', 2, 'stuks'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Ui', 1, 'stuks'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Knoflook', 2, 'teentjes'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Kerriepoeder', 2, 'theelepels'),
          ('Pannenkoeken', 'admin@admin.nl', 'Bloem', 250, 'g'),
          ('Pannenkoeken', 'admin@admin.nl', 'Melk', 500, 'ml'),
          ('Pannenkoeken', 'admin@admin.nl', 'Ei', 2, 'stuks'),
          ('Pannenkoeken', 'admin@admin.nl', 'Boter', 25, 'g'),
          ('Pannenkoeken', 'admin@admin.nl', 'Zout', 1, 'snufje')
     ) AS seed(recipe_title, owner_email, ingredient_name, amount, unit)
         JOIN users u ON u.email = seed.owner_email
         JOIN profiles p ON p.user_id = u.id
         JOIN recipes r ON r.title = seed.recipe_title AND r.owner_profile_id = p.id
         JOIN ingredients i ON i.name = seed.ingredient_name
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.ingredient_id = i.id
);

INSERT INTO recipe_categories (recipe_id, category_id)
SELECT r.id, c.id
FROM (VALUES
          ('Pasta met spinazie en tomaat', 'user@user.com', 'Pasta'),
          ('Pasta met spinazie en tomaat', 'user@user.com', 'Gezond'),
          ('Omelet met groenten', 'user@user.com', 'Ontbijt'),
          ('Omelet met groenten', 'user@user.com', 'Snel'),
          ('Vegetarische tomatensoep', 'user@user.com', 'Soep'),
          ('Vegetarische tomatensoep', 'user@user.com', 'Vegetarisch'),
          ('Zalm met aardappelen', 'user@user.com', 'Vis'),
          ('Zalm met aardappelen', 'user@user.com', 'Diner'),
          ('Kip met rijst en broccoli', 'user2@user2.com', 'Kip'),
          ('Kip met rijst en broccoli', 'user2@user2.com', 'Diner'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Mexicaans'),
          ('Mexicaanse wraps', 'user2@user2.com', 'Diner'),
          ('Risotto met champignons', 'user2@user2.com', 'Italiaans'),
          ('Risotto met champignons', 'user2@user2.com', 'Vegetarisch'),
          ('Salade met mozzarella', 'user2@user2.com', 'Salade'),
          ('Salade met mozzarella', 'user2@user2.com', 'Lunch'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Italiaans'),
          ('Lasagne uit de oven', 'admin@admin.nl', 'Diner'),
          ('Romige pasta met zalm', 'admin@admin.nl', 'Pasta'),
          ('Romige pasta met zalm', 'admin@admin.nl', 'Vis'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Aziatisch'),
          ('Groentecurry met kokosmelk', 'admin@admin.nl', 'Vegetarisch'),
          ('Pannenkoeken', 'admin@admin.nl', 'Ontbijt'),
          ('Pannenkoeken', 'admin@admin.nl', 'Lunch')
     ) AS seed(recipe_title, owner_email, category_name)
         JOIN users u ON u.email = seed.owner_email
         JOIN profiles p ON p.user_id = u.id
         JOIN recipes r ON r.title = seed.recipe_title AND r.owner_profile_id = p.id
         JOIN categories c ON c.name = seed.category_name
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_categories rc WHERE rc.recipe_id = r.id AND rc.category_id = c.id
);
