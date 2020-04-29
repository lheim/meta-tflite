DESCRIPTION = "TensorFlow Lite C++ Library"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=64a34301f8e355f57ec992c2af3e5157"

SRCREV_tensorflow = "f270180a6caa8693f2b2888ac7e6b8e69c4feaa8"

SRC_URI = " \
	git://github.com/tensorflow/tensorflow.git;branch=r2.1;name=tensorflow \
	file://0001-Use-wget-instead-of-curl.patch \
	file://0002-Tailor-Makefile-to-armv7l.patch \
	file://Makefile \
"

PR = "r0"

COMPATIBLE_MACHINE = "(colibri-imx7-emmc)"

S = "${WORKDIR}/git"

PACKAGES += "${PN}-examples ${PN}-examples-dbg"
RDEPENDS_${PN}-examples += "${PN}"
RDEPENDS_${PN}-examples-dbg += "${PN}"

DEPENDS = "gzip-native unzip-native zlib"

do_configure(){
	export HTTP_PROXY=${HTTP_PROXY}
	export HTTPS_PROXY=${HTTPS_PROXY}
	export http_proxy=${HTTP_PROXY}
	export https_proxy=${HTTPS_PROXY}
	
	# Download dependencies for compiling
	${S}/tensorflow/lite/tools/make/download_dependencies.sh

	install -m 0750 ${WORKDIR}/Makefile ${S}/
}


CXXFLAGS += "--std=c++11"
FULL_OPTIMIZATION += "-O3 -DNDEBUG"

do_install(){
	install -d ${D}${libdir}
	cp -r \
		${S}/tensorflow/lite/tools/make/gen/lib/* \
		${D}${libdir}

	cd ${S}
	find tensorflow/lite -name "*.h" | cpio -pdm ${D}${includedir}/
	find tensorflow/lite -name "*.inc" | cpio -pdm ${D}${includedir}/

	install -d ${D}${includedir}/tensorflow_lite
	cd ${S}/tensorflow/lite
	cp --parents \
		$(find . -name "*.h*") \
		${D}${includedir}/tensorflow_lite

	install -d ${D}${bindir}/${PN}-${PV}/
	install -m 0555 \
                ${S}/tensorflow/lite/tools/make/gen/bin/* \
                ${D}${bindir}/${PN}-${PV}/
	cd ${D}${bindir}
	ln -sf ${PN}-${PV} ${PN}
}

ALLOW_EMPTY_${PN} = "1"

FILES_${PN} = ""

FILES_${PN}-dev = " \
	${includedir} \
"

FILES_${PN}-staticdev = " \
	${libdir} \
"

FILES_${PN}-dbg = " \
	/usr/src/debug/tensorflow-lite \
"

FILES_${PN}-examples = " \
	${bindir}/${PN} \
	${bindir}/${PN}-${PV}/minimal \
	${bindir}/${PN}-${PV}/benchmark_model \
	${bindir}/${PN}-${PV}/benchmark_model_performance_options \
"

FILES_${PN}-examples-dbg = " \
	${bindir}/${PN}-${PV}/.debug \
"
