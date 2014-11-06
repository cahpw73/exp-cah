
--@author Christian Alba

DROP VIEW v_purchase_order;
CREATE OR REPLACE VIEW v_purchase_order AS
SELECT row_number() OVER () as id, po.id as po_id,
	po.project, po.po, po.variation, po.po_title, s.supplier,po.responsible_expediting
   FROM purchase_order  po inner join supplier s on po.id=s.purchase_order_id
   WHERE po.status_id<>3
   ORDER BY po.id ;