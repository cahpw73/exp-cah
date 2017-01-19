ALTER TABLE text_snippet alter column text_snippet DROP NOT NULL;
ALTER TABLE project_text_snippet alter column description DROP NOT NULL;
ALTER TABLE clauses_text alter column clauses DROP NOT NULL;
ALTER TABLE main_document alter column description DROP NOT NULL;
ALTER TABLE project_document alter column description DROP NOT NULL;
ALTER TABLE po_document alter column description DROP NOT NULL;

update text_snippet set text_snippet = description_snippet;
update project_text_snippet set description = description_snippet;
update clauses_text set clauses = description_snippet;
update main_document set description = description_document;
update project_document set description = description_document;
update po_document set description = description_document;

ALTER TABLE text_snippet  ALTER COLUMN text_snippet TYPE varchar(100000);
ALTER TABLE project_text_snippet  ALTER COLUMN description TYPE varchar(100000);
ALTER TABLE clauses_text  ALTER COLUMN clauses TYPE varchar(100000);
ALTER TABLE main_document  ALTER COLUMN description TYPE varchar(100000);
ALTER TABLE project_document  ALTER COLUMN description TYPE varchar(100000);
ALTER TABLE po_document  ALTER COLUMN description TYPE varchar(100000);

select * 
from purchase_order 
where po_id in (select id from p_order where delivery_instruction like '%Minera PanamÃ¡%' order by id desc)
order by po


UPDATE p_order SET delivery_instruction = convert_from(convert(BYTEA 'Minera PanamÃ¡ S.A.
Cobre Panama Project
Donoso, PanamÃ¡
', 'utf-8', 'latin-1'), 'utf-8') WHERE delivery_instruction like '%Minera PanamÃ¡%';
