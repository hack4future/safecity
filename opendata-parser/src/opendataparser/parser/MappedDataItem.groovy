package opendataparser.parser

/**
 * User: pyotruk
 * Date: 2013-11-17
 */
class MappedDataItem {

    Date date
    String address
    String description

    static dataMapping = [
            'address' : ['административный район', 'адрес'],
            'description' : ['характер происшествия', 'причина пожара', 'что горело']
    ]

    Map getDataMap() {
        return [
                'date': date.format('yyyy-MM-dd'),
                'address': address,
                'description': description
        ]
    }

}
