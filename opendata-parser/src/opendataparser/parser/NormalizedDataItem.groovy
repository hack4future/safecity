package opendataparser.parser

/**
 * User: pyotruk
 * Date: 2013-11-17
 */
class NormalizedDataItem {

    String GUID
    Date date
    String type // type of accident
    String district
    String street
    String building
    Double latitude = 0
    Double longitude = 0
    String description
    String zhes // ЖЭС
    String cause

    Map getDataMap() {
        return [
                'GUID': GUID,
                'date': date.format('yyyy-MM-dd'),
                'type': type,
                'district': district,
                'street': street,
                'building': building,
                'latitude': latitude,
                'longitude': longitude,
                'description': description,
                'zhes': zhes,
                'cause': cause
        ]
    }

}
