maxCheckstyleErrors=1704

./gradlew core:checkstyleMain > checkstyleOutput 2>&1
grep -o "warning" checkstyleOutput | wc -w > wcResult
read errors filename < wcResult
rm wcResult
echo "found $errors checkstyle errors "
echo "current max is $maxCheckstyleErrors "
rm ./checkstyleOutput
if [ "$errors" -gt "$maxCheckstyleErrors" ]
then
	echo "Checkstyle FAILED"
	exit 1
else
	echo "Checkstyle SUCCEEDED"
	exit 0
fi

