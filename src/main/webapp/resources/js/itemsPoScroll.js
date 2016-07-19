function autoScrollPDatatable(idDataTable){
    console.log("autoScrollPDatatable(idDataTable["+idDataTable+"])");
    var primfcid = idDataTable.replace(':', '\\:');
    var idDataTbl = '#' + primfcid;
    //var scrollTopInPx = $(idDataTbl + " .ui-datatable-scrollable-body").height();
    var idDataContainer = idDataTbl  + "_data";
    var lichildren = $(idDataContainer).children("tr");
    var itemHeight = $(idDataContainer).children("tr").height();
    var scrollTopInPx = lichildren.length * itemHeight;
    console.log("ScrollTop Height = " + scrollTopInPx);
    $(idDataTbl + " .ui-datatable-scrollable-body").animate({scrollTop:scrollTopInPx}, scrollTopInPx);
}

